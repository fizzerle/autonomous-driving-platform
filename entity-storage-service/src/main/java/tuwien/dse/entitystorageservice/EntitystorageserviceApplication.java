package tuwien.dse.entitystorageservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
@EnableDiscoveryClient
@SpringBootApplication
public class EntitystorageserviceApplication {

    public static void main(String[] args) {
        SpringApplication.run(EntitystorageserviceApplication.class, args);
    }

}
