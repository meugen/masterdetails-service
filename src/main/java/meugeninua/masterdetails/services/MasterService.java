package meugeninua.masterdetails.services;

import meugeninua.masterdetails.dto.MasterDto;
import meugeninua.masterdetails.mappers.DetailEntityMapper;
import meugeninua.masterdetails.mappers.MasterEntityMapper;
import meugeninua.masterdetails.repositories.MasterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;

@Service
public class MasterService {

    @Autowired
    private MasterRepository masterRepository;
    @Autowired
    private MasterEntityMapper masterMapper;
    @Autowired
    private DetailEntityMapper detailMapper;

    public MasterDto findById(Long id) {
        return masterRepository.findById(id)
            .map(masterMapper::mapToDto)
            .orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND)
            );
    }

    @Transactional
    public MasterDto create(MasterDto master) {
        var details = master.getDetails();
        master.setDetails(new ArrayList<>());
        var masterEntity = masterMapper.mapToEntity(master);
        masterEntity.setId(null);
        masterEntity.setDetails(detailMapper.mapToEntityList(masterEntity, details));
        masterEntity = masterRepository.save(masterEntity);
        return masterMapper.mapToDto(masterEntity);
    }

    @Transactional
    public MasterDto update(Long id, MasterDto master) {
        if (!masterRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        var details = master.getDetails();
        master.setDetails(new ArrayList<>());
        var masterEntity = masterMapper.mapToEntity(master);
        masterEntity.setId(id);
        masterEntity.setDetails(detailMapper.mapToEntityList(masterEntity, details));
        masterEntity = masterRepository.save(masterEntity);
        return masterMapper.mapToDto(masterEntity);
    }

    @Transactional
    public void deleteById(Long id) {
        var master = masterRepository.findById(id).orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND)
        );
        masterRepository.delete(master);
    }
}
