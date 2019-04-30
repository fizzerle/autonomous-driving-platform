package tuwien.dse.eventstorageservice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import springfox.documentation.swagger2.annotations.EnableSwagger2;
import tuwien.dse.eventstorageservice.persistence.EventRepository;
import tuwien.dse.eventstorageservice.services.EventStorageController;

@EnableDiscoveryClient
@SpringBootApplication
@EnableSwagger2
public class EventstorageserviceApplication {


    private static final Logger LOGGER = LoggerFactory.getLogger(EventstorageserviceApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(EventstorageserviceApplication.class, args
        );
        LOGGER.info("Version 01");
    }

}
