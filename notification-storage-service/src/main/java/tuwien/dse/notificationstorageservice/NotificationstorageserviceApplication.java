package tuwien.dse.notificationstorageservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableDiscoveryClient
@SpringBootApplication
@EnableSwagger2
public class NotificationstorageserviceApplication {

    /**
     * Start the Application for the Notificationstorageservice.
     *
     * @param args Arguments.
     */
    public static void main(String[] args) {
        SpringApplication.run(NotificationstorageserviceApplication.class, args);
    }

}
