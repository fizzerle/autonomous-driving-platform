package tuwien.dse.apigateway;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class FischService {

    private RestTemplate restTemplate = new RestTemplate();

    @HystrixCommand(fallbackMethod = "defaultFisch")
    public String getFisch() {
        return restTemplate.getForObject("http://entity-storage/failstorage/fisch", String.class);
    }

    @HystrixCommand(fallbackMethod = "defaultDeadFisch")
    public String getDeadFisch() {
        return restTemplate.getForObject("http://entity-storage/failstorage/dead", String.class);
    }

    private String defaultFisch() {
        return "Sorry, hystrix fisch";
    }

    private String defaultDeadFisch() {
        return "Sorry, hystrix dead fisch";
    }
}
