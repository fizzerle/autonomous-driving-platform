package tuwien.dse.apigateway;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.util.pattern.PathPattern;
import tuwien.dse.apigateway.cache.RedisService;

import java.util.Map;

@RestController
public class FallbackRestController {

    private static final Logger LOGGER = LoggerFactory.getLogger(FallbackRestController.class);

    @Autowired
    private RedisService redisService;

    @RequestMapping(value = "/hystrixfallback",produces = MediaType.APPLICATION_JSON_VALUE)
    public String fallback(ServerWebExchange serverWebExchange) {
        LOGGER.warn("Circute Breaker");
        /*LOGGER.info("ORIGIN: {}", headers.getOrigin());
        LOGGER.info("LOCATION: {}", headers.getLocation());*/

        //Get the PathMatchInfo
        PathPattern.PathMatchInfo pathMatchInfo = serverWebExchange.getAttribute(ServerWebExchangeUtils.GATEWAY_ORIGINAL_REQUEST_URL_ATTR);

        //Get the template variables
        Map<String, String> urlTemplateVariables = pathMatchInfo.getUriVariables();

        for (Map.Entry<String, String> entry : urlTemplateVariables.entrySet()) {
            LOGGER.info(""+entry.getKey()+" " + entry.getValue());
        }
        return redisService.getCache("/entitystorage/oem");
    }
}
