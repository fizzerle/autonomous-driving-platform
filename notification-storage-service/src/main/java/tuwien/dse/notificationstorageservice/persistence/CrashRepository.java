package tuwien.dse.notificationstorageservice.persistence;

import org.springframework.data.mongodb.repository.MongoRepository;
import tuwien.dse.notificationstorageservice.model.CrashEvent;

import java.util.List;

/**
 * MongoRepository to access Crashes in the database.
 */
public interface CrashRepository extends MongoRepository<CrashEvent,String> {

    List<CrashEvent> findAll();
}
