package meugeninua.masterdetails.services;

import meugeninua.masterdetails.dto.MasterDto;
import meugeninua.masterdetails.mappers.DetailEntityMapper;
import meugeninua.masterdetails.mappers.MasterEntityMapper;
import meugeninua.masterdetails.prrocessors.Processor;
import meugeninua.masterdetails.repositories.MasterRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Service
public class MasterService {

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

    public Stream<?> findAll() {
        var stream = StreamSupport.stream(
            masterRepository.findAll().spliterator(),
            false
        );
        return stream.map(masterMapper::mapToDto).map(masterProcessor::process);
    }

    public Object findById(Long id) {
        return masterRepository.findById(id)
            .map(masterMapper::mapToDto)
            .map(masterProcessor::process)
            .orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND)
            );
    }

    @Transactional
    public Object create(MasterDto master) {
        var details = master.getDetails();
        master.setDetails(new ArrayList<>());
        var masterEntity = masterMapper.mapToEntity(master);
        masterEntity.setId(null);
        masterEntity.setDetails(detailMapper.mapToEntityList(masterEntity, details));
        masterEntity = masterRepository.save(masterEntity);

        var result = masterMapper.mapToDto(masterEntity);
        return masterProcessor.process(result);
    }

    @Transactional
    public Object update(Long id, MasterDto master) {
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

    @Transactional
    public void deleteById(Long id) {
        var master = masterRepository.findById(id).orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND)
        );
        masterRepository.delete(master);
    }
}
