package tuwien.dse.apigateway;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tuwien.dse.apigateway.cache.RedisService;

@RestController
public class FallbackRestController {

    private static final Logger LOGGER = LoggerFactory.getLogger(FallbackRestController.class);

    @Autowired
    private RedisService redisService;

    @RequestMapping(value = "/hystrixfallback",produces = MediaType.APPLICATION_JSON_VALUE)
    public String fallback(@RequestHeader HttpHeaders headers) {
        LOGGER.warn("Circute Breaker");
        LOGGER.info("ORIGIN: {}", headers.getOrigin());
        LOGGER.info("LOCATION: {}", headers.getLocation());
        //LOGGER.info("URI: {}", request.getURI());
        LOGGER.info("Request: ", headers);
        return redisService.getCache("/entitystorage/oem");
    }
}
