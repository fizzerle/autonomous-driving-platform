package tuwien.dse.eventstorageservice.persistence;

import org.springframework.data.mongodb.repository.MongoRepository;
import tuwien.dse.eventstorageservice.model.Event;

import java.util.List;
import java.util.Optional;

public interface EventRepository extends MongoRepository<Event,String> {

    List<Event> findAll();
    List<Event> findAllByChassisnumber(String chassisnumber);
    List<Event> findAllByChassisnumberOrderByTimestampDesc(String chassisnumber);
    Optional<Event> findById(String id);
}
