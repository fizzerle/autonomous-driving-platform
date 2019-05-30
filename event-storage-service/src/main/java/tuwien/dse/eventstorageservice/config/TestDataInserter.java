package tuwien.dse.eventstorageservice.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tuwien.dse.eventstorageservice.model.Event;
import tuwien.dse.eventstorageservice.model.Location;
import tuwien.dse.eventstorageservice.persistence.EventRepository;

import java.util.Date;

@Configuration
public class TestDataInserter {

    private static final Logger log = LoggerFactory.getLogger(TestDataInserter.class);

    @Bean
    public CommandLineRunner insertTestData(EventRepository repo) {
        return args -> {
            if (repo.findAll().size() > 0) {
                return;
            }

            repo.save(new Event(new Location(10, 10), "ABCD0", new Date(0), 50, 10, 10, null, 1));
            repo.save(new Event(new Location(10, 11), "ABCD0", new Date(1), 50, 10, 10, null, 1));
            repo.save(new Event(new Location(10, 12), "ABCD0", new Date(2), 50, 10, 10, null, 1));
            repo.save(new Event(new Location(10, 13), "ABCD0", new Date(3), 50, 10, 10, null, 1));

            repo.save(new Event(new Location(11, 10), "ABCD1", new Date(0), 50, 10, 10, null, 1));
            repo.save(new Event(new Location(11, 10), "ABCD1", new Date(2), 50, 10, 10, null, 1));

            repo.save(new Event(new Location(12, 10), "ABCD2", new Date(0), 50, 10, 10, null, 2));
            repo.save(new Event(new Location(12, 10), "ABCD2", new Date(1), 60, 5, 20, null, 2));

            repo.save(new Event(new Location(13, 10), "ABCD3", new Date(0), 50, 10, 10, null, 2));
            repo.save(new Event(new Location(13, 10), "ABCD3", new Date(1), 50, 10, 10, null, 2));

            repo.save(new Event(new Location(16, 10), "ABCD6", new Date(0), 50, 10, 10, null, 4));
            repo.save(new Event(new Location(16, 10), "ABCD6", new Date(1), 50, 10, 10, null, 4));
            repo.save(new Event(new Location(16, 10), "ABCD6", new Date(2), 50, 10, 10, null, 4));

            repo.save(new Event(new Location(18, 10), "ABCD8", new Date(0), 50, 10, 10, null, 1));
            repo.save(new Event(new Location(18, 10), "ABCD8", new Date(1), 50, 10, 10, null, 1));

            repo.save(new Event(new Location(19, 10), "ABCD9", new Date(0), 50, 10, 10, null, 1));
            repo.save(new Event(new Location(19, 10), "ABCD9", new Date(1), 50, 10, 10, null, 1));
            repo.save(new Event(new Location(19, 10), "ABCD9", new Date(2), 50, 10, 10, null, 1));
            Event crash = new Event(new Location(19, 10), "ABCD9", new Date(3), 50, 10, 10, "Tree Crash", 1);
            crash.setId("CRASH1");
            repo.save(crash);

            log.info("Inserted TestData");
        };
    }
}
