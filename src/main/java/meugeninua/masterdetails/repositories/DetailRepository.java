package meugeninua.masterdetails.repositories;

import meugeninua.masterdetails.entities.Detail;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DetailRepository extends CrudRepository<Detail, Long> {

    Iterable<Detail> findAllByMasterIdEquals(Long masterId);

    Optional<Detail> findByMasterIdEqualsAndIdEquals(Long masterId, Long id);

    boolean existsByMasterIdEqualsAndIdEquals(Long masterId, Long id);
}
