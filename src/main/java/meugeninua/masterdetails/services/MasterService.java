package meugeninua.masterdetails.services;

import meugeninua.masterdetails.caching.CachingConstants;
import meugeninua.masterdetails.dto.MasterDto;
import meugeninua.masterdetails.mappers.DetailEntityMapper;
import meugeninua.masterdetails.mappers.MasterEntityMapper;
import meugeninua.masterdetails.processors.Processor;
import meugeninua.masterdetails.repositories.MasterRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Map;
import java.util.stream.StreamSupport;

@Service
public class MasterService implements CachingConstants {

    private final MasterRepository masterRepository;
    private final MasterEntityMapper masterMapper;
    private final DetailEntityMapper detailMapper;
    private final Processor masterProcessor;

    public MasterService(
        MasterRepository masterRepository,
        MasterEntityMapper masterMapper,
        DetailEntityMapper detailMapper,
        @Qualifier("master") Processor masterProcessor
    ) {
        this.masterRepository = masterRepository;
        this.masterMapper = masterMapper;
        this.detailMapper = detailMapper;
        this.masterProcessor = masterProcessor;
    }

    @Cacheable(value = CACHE_MASTERS_LIST, keyGenerator = "noKey")
    public Iterable<Map<String, Object>> findAll() {
        var stream = StreamSupport.stream(
            masterRepository.findAll().spliterator(),
            false
        );
        return stream.map(masterMapper::mapToDto)
            .map(masterProcessor::process)
            .toList();
    }

    @Cacheable(value = CACHE_MASTER_BY_ID, key = "#id")
    public Map<String, Object> findById(Long id) {
        return masterRepository.findById(id)
            .map(masterMapper::mapToDto)
            .map(masterProcessor::process)
            .orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND)
            );
    }

    /**
     * Create master resource
     * @see meugeninua.masterdetails.caching.MasterCacheEvictProcessor to evict outdated cache after create
     * @param master Body
     * @return Created master content
     */
    @Transactional
    @CachePut(value = CACHE_MASTER_BY_ID, key = "#result['id']")
    public Map<String, Object> create(MasterDto master) {
        var details = master.getDetails();
        master.setDetails(new ArrayList<>());
        var masterEntity = masterMapper.mapToEntity(master);
        masterEntity.setId(null);
        masterEntity.setDetails(detailMapper.mapToEntityList(masterEntity, details));
        masterEntity = masterRepository.save(masterEntity);

        var result = masterMapper.mapToDto(masterEntity);
        return masterProcessor.process(result);
    }

    /**
     * Update master resource
     * @see meugeninua.masterdetails.caching.MasterCacheEvictProcessor to evict outdated cache after update
     * @param id Master id to update
     * @param master Body
     * @return Updated master content
     */
    @Transactional
    @CachePut(value = CACHE_MASTER_BY_ID, key = "#result['id']")
    public Map<String, Object> update(Long id, MasterDto master) {
        if (!masterRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        var details = master.getDetails();
        master.setDetails(new ArrayList<>());
        var masterEntity = masterMapper.mapToEntity(master);
        masterEntity.setId(id);
        masterEntity.setDetails(detailMapper.mapToEntityList(masterEntity, details));
        masterEntity = masterRepository.save(masterEntity);

        var result = masterMapper.mapToDto(masterEntity);
        return masterProcessor.process(result);
    }

    /**
     * Delete master resource
     * @see meugeninua.masterdetails.caching.MasterCacheEvictProcessor to evict outdated cache after delete
     * @param id Master id
     */
    @Transactional
    public void deleteById(Long id) {
        var master = masterRepository.findById(id).orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND)
        );
        var dto = masterMapper.mapToDto(master);
        masterRepository.delete(master);
        masterProcessor.process(dto);
    }
}
