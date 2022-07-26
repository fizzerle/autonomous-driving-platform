package tuwien.dse.entitystorageservice.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tuwien.dse.entitystorageservice.model.Car;
import tuwien.dse.entitystorageservice.persistence.CarRepository;

@Configuration
public class TestDataInserter {

    private static final Logger log = LoggerFactory.getLogger(TestDataInserter.class);

    /**
     * Inserts testdata into the EntityRepository.
     * The cars used in the test scenario are created.
     *
     * @param repo CarRepository to save cars
     * @return void
     */
    @Bean
    public CommandLineRunner insertTestData(@Autowired CarRepository repo) {
        return args -> {
            if (repo.findAll().size() > 0) {
                return;
            }

            // Simulation Cars
            repo.save(new Car("000", "Audi", "A8"));
            repo.save(new Car("001", "BMW", "i8 Coupe"));
            repo.save(new Car("002", "Opel", "Astra"));
            repo.save(new Car("003", "Audi", "Q2"));
            repo.save(new Car("004", "Audi", "r8"));
            repo.save(new Car("005", "BMW", "x7"));

            log.info("Inserted Test Data");
        };
    }
}
