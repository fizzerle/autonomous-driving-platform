package tuwien.dse.apigateway;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpRequest;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FallbackRestController {

    private static final Logger LOGGER = LoggerFactory.getLogger(FallbackRestController.class);

    @RequestMapping("/hystrixfallback")
    public String fallback(Throwable throwable) {
        LOGGER.warn("Circuit Braker called ... WHAAAT ?!?!? With Error: {}", throwable);
        return throwable.getMessage();
    }
}
