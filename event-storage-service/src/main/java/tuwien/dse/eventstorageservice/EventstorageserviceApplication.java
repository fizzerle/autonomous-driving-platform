package tuwien.dse.eventstorageservice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableDiscoveryClient
@SpringBootApplication
@EnableSwagger2
@EnableCircuitBreaker
public class EventstorageserviceApplication {


    private static final Logger LOGGER = LoggerFactory.getLogger(EventstorageserviceApplication.class);

    /**
     * Starts the application for the EventStorageService.
     *
     * @param args Arguments.
     */
    public static void main(String[] args) {
        SpringApplication.run(EventstorageserviceApplication.class, args
        );
        LOGGER.info("Version 01");
    }

}
