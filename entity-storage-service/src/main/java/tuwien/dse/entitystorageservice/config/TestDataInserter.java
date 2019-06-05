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

            // Other test data
            /*repo.save(new Car("ABCD0", "Audi", "A8"));
            repo.save(new Car("ABCD1", "Audi", "A6"));
            repo.save(new Car("ABCD2", "Audi", "A5"));
            repo.save(new Car("ABCD3", "BMW", "z3"));
            repo.save(new Car("ABCD4", "BMW", "z4"));
            repo.save(new Car("ABCD5", "BMW", "z5"));
            repo.save(new Car("ABCD6", "VW", "K1"));
            repo.save(new Car("ABCD7", "VW", "Golf"));
            repo.save(new Car("ABCD8", "Fiat", "Punto"));
            repo.save(new Car("ABCD9", "Fiat", "Punto"));*/

            log.info("Inserted Test Data");
        };
    }
}
