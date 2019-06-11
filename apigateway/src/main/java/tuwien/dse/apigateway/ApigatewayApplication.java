package tuwien.dse.apigateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
@EnableDiscoveryClient
@EnableCircuitBreaker
public class ApigatewayApplication {

    /**
     * Starts the Apllication for the ApiGateway.
     * @param args args
     */
    public static void main(String[] args) {
        SpringApplication.run(ApigatewayApplication.class, args);
    }

}
