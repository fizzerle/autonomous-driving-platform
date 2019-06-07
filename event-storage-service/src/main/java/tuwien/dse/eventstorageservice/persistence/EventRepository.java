package tuwien.dse.eventstorageservice.persistence;

import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import tuwien.dse.eventstorageservice.model.Event;

import java.util.List;
import java.util.Optional;

/**
 * MongoRepository to access the events in the database.
 */
public interface EventRepository extends MongoRepository<Event,String> {

    List<Event> findAll();

    List<Event> findAllByChassisnumberOrderByTimestampDesc(String chassisnumber);

    Optional<Event> findById(String id);

    List<Event> findByLocationNearOrderByTimestampDesc(Point point, Distance distance);
}
