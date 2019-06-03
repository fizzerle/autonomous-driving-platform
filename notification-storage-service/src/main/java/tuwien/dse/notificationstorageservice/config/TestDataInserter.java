package tuwien.dse.notificationstorageservice.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tuwien.dse.notificationstorageservice.model.CrashEvent;
import tuwien.dse.notificationstorageservice.persistence.CrashRepository;

import java.util.Date;


public class TestDataInserter {

    private static final Logger log = LoggerFactory.getLogger(TestDataInserter.class);


    public CommandLineRunner insertTestData(CrashRepository repo) {
        return args -> {
            if (repo.findAll().size() > 0) {
                return;
            }

            CrashEvent crash = new CrashEvent("CRASH1", "ABCD9", new Date(3), null, "Tree Crash");
            crash.setCrashId("C1");
            repo.save(crash);

            log.info("Inserted Test Data");
        };
    }
}
