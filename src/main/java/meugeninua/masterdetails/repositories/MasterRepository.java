package meugeninua.masterdetails.repositories;

import meugeninua.masterdetails.entities.Master;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MasterRepository extends CrudRepository<Master, Long> {
}
