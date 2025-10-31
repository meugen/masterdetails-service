package meugeninua.masterdetails.services;

import meugeninua.masterdetails.dto.DetailDto;
import meugeninua.masterdetails.mappers.DetailEntityMapper;
import meugeninua.masterdetails.repositories.DetailRepository;
import meugeninua.masterdetails.repositories.MasterRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Service
public class DetailService {

    private MasterRepository masterRepository;
    private final DetailRepository detailRepository;
    private final DetailEntityMapper detailMapper;

    public DetailService(
        MasterRepository masterRepository,
        DetailRepository detailRepository,
        DetailEntityMapper detailMapper
    ) {
        this.masterRepository = masterRepository;
        this.detailRepository = detailRepository;
        this.detailMapper = detailMapper;
    }

    public Stream<DetailDto> findAll(Long masterId) {
        if (!masterRepository.existsById(masterId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        var stream = StreamSupport.stream(
            detailRepository.findAllByMasterIdEquals(masterId).spliterator(),
            false
        );
        return stream.map(detailMapper::mapToDto);
    }

    public DetailDto findById(Long masterId, Long detailId) {
        return detailRepository.findByMasterIdEqualsAndIdEquals(masterId, detailId)
            .map(detailMapper::mapToDto)
            .orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND)
            );
    }

    @Transactional
    public DetailDto create(Long masterId, DetailDto detailDto) {
        var master = masterRepository.findById(masterId).orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND)
        );
        var entity = detailMapper.mapToEntity(master, detailDto);
        entity.setId(null);
        entity = detailRepository.save(entity);

        return detailMapper.mapToDto(entity);
    }

    @Transactional
    public DetailDto update(Long masterId, Long detailId, DetailDto detailDto) {
        if (!detailRepository.existsByMasterIdEqualsAndIdEquals(masterId, detailId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        var master = masterRepository.findById(masterId).orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND)
        );
        var entity = detailMapper.mapToEntity(master, detailDto);
        entity.setId(detailId);
        entity = detailRepository.save(entity);

        return detailMapper.mapToDto(entity);
    }

    @Transactional
    public void deleteById(Long masterId, Long detailId) {
        var detail = detailRepository.findByMasterIdEqualsAndIdEquals(masterId, detailId)
            .orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND)
            );
        detailRepository.delete(detail);
    }
}
