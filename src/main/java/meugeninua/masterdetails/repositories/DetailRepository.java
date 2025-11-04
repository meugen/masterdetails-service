package meugeninua.masterdetails.repositories;

import meugeninua.masterdetails.entities.Detail;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DetailRepository extends CrudRepository<Detail, Long> {

    @Query("select d from Detail d join fetch d.master where d.master.id=:masterId")
    Iterable<Detail> findAllByMasterId(Long masterId);

    @Query("select d from Detail d join fetch d.master where d.master.id=:masterId and d.id=:id")
    Optional<Detail> findByMasterIdAndId(Long masterId, Long id);

    boolean existsByMasterIdEqualsAndIdEquals(Long masterId, Long id);
}
