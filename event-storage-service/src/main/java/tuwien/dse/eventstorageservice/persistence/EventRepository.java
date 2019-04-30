package tuwien.dse.eventstorageservice.persistence;

import org.springframework.data.mongodb.repository.MongoRepository;
import tuwien.dse.eventstorageservice.model.Event;

import java.util.List;
import java.util.Optional;

public interface EventRepository extends MongoRepository<Event,String> {

    public List<Event> findAll();
    public List<Event> findAllByChassisnumber(String chassisnumber);
    public Optional<Event> findById(String id);
}
