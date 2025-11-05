package meugeninua.masterdetails.services;

import meugeninua.masterdetails.caching.CachingConstants;
import meugeninua.masterdetails.dto.DetailDto;
import meugeninua.masterdetails.mappers.DetailEntityMapper;
import meugeninua.masterdetails.processors.Processor;
import meugeninua.masterdetails.repositories.DetailRepository;
import meugeninua.masterdetails.repositories.MasterRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;
import java.util.stream.StreamSupport;

@Service
public class DetailService implements CachingConstants {

    private final MasterRepository masterRepository;
    private final DetailRepository detailRepository;
    private final DetailEntityMapper detailMapper;
    private final Processor detailProcessor;

    public DetailService(
        MasterRepository masterRepository,
        DetailRepository detailRepository,
        DetailEntityMapper detailMapper,
        @Qualifier("detail") Processor detailProcessor
    ) {
        this.masterRepository = masterRepository;
        this.detailRepository = detailRepository;
        this.detailMapper = detailMapper;
        this.detailProcessor = detailProcessor;
    }

    @Cacheable(value = CACHE_DETAILS_LIST, key = "#masterId")
    public Iterable<Map<String, Object>> findAll(Long masterId) {
        if (!masterRepository.existsById(masterId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        var stream = StreamSupport.stream(
            detailRepository.findAllByMasterId(masterId).spliterator(),
            false
        );
        return stream.map(detailMapper::mapToDto)
            .map(detailProcessor::process)
            .toList();
    }

    @Cacheable(value = CACHE_DETAIL_BY_ID, key = "#masterId+'/'+#detailId")
    public Map<String, Object> findById(Long masterId, Long detailId) {
        return detailRepository.findByMasterIdAndId(masterId, detailId)
            .map(detailMapper::mapToDto)
            .map(detailProcessor::process)
            .orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND)
            );
    }

    /**
     * Create detail resource
     * @see meugeninua.masterdetails.caching.DetailCacheEvictProcessor to evict outdated cache after create
     * @param masterId Master id
     * @param detailDto Body
     * @return Created detail content
     */
    @Transactional
    @CachePut(value = CACHE_DETAIL_BY_ID, key = "#result['masterId']+'/'+#result['id']")
    public Map<String, Object> create(Long masterId, DetailDto detailDto) {
        var master = masterRepository.findById(masterId).orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND)
        );
        var entity = detailMapper.mapToEntity(master, detailDto);
        entity.setId(null);
        entity = detailRepository.save(entity);

        var result = detailMapper.mapToDto(entity);
        return detailProcessor.process(result);
    }

    /**
     * Update detail resource
     * @see meugeninua.masterdetails.caching.DetailCacheEvictProcessor to evict outdated cache after update
     * @param masterId Master id
     * @param detailId Detail id to update
     * @param detailDto Body
     * @return Updated detail content
     */
    @Transactional
    @CachePut(value = CACHE_DETAIL_BY_ID, key = "#result['masterId']+'/'+#result['id']")
    public Map<String, Object> update(Long masterId, Long detailId, DetailDto detailDto) {
        if (!detailRepository.existsByMasterIdEqualsAndIdEquals(masterId, detailId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        var master = masterRepository.findById(masterId).orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND)
        );
        var entity = detailMapper.mapToEntity(master, detailDto);
        entity.setId(detailId);
        entity = detailRepository.save(entity);

        var result = detailMapper.mapToDto(entity);
        return detailProcessor.process(result);
    }

    /**
     * Delete detail resource
     * @see meugeninua.masterdetails.caching.DetailCacheEvictProcessor to evict outdated cache after delete
     * @param masterId Master id
     * @param detailId Detail id to delete
     */
    @Transactional
    public void deleteById(Long masterId, Long detailId) {
        var detail = detailRepository.findByMasterIdAndId(masterId, detailId)
            .orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND)
            );
        var dto = detailMapper.mapToDto(detail);
        detailRepository.delete(detail);
        detailProcessor.process(dto);
    }
}
