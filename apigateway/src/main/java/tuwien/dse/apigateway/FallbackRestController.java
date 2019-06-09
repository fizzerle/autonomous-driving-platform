package tuwien.dse.apigateway;

import com.sun.xml.internal.ws.client.sei.ResponseBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.cloud.client.loadbalancer.reactive.Response;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.http.*;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.util.pattern.PathPattern;
import tuwien.dse.apigateway.cache.RedisService;

import java.net.URI;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

@RestController
public class FallbackRestController {

    private static final Logger LOGGER = LoggerFactory.getLogger(FallbackRestController.class);

    @Autowired
    private RedisService redisService;

    @RequestMapping(
            value = "/cacheFallback",
            produces = MediaType.APPLICATION_JSON_VALUE,
            method = RequestMethod.GET
    )
    public String fallback(ServerWebExchange serverWebExchange) {
        Set<URI> uris = serverWebExchange.getAttributeOrDefault(ServerWebExchangeUtils.GATEWAY_ORIGINAL_REQUEST_URL_ATTR, Collections.emptySet());
        String originalUri = (uris.isEmpty()) ? "Unknown" : uris.iterator().next().toString();
        String[] parts = originalUri.split("/", 4);
        if (parts.length >= 4) {
            originalUri = "/" + parts[3];
        }
        if(originalUri.contains("websocket")) return "websocket unavailable";
        LOGGER.info("Looking for cached entry for {}", originalUri);
        return redisService.getCache(originalUri);
    }


    @RequestMapping( 
            value = "/cacheFallback",
            method = {
                    RequestMethod.DELETE,
                    RequestMethod.POST,
                    RequestMethod.PATCH,
                    RequestMethod.PUT
            }
    )
    @ResponseBody
    public ResponseEntity failureFallback() {
        return new ResponseEntity(HttpStatus.SERVICE_UNAVAILABLE);
    }

    /*@RequestMapping(value = "/hystrixfallback",produces = MediaType.APPLICATION_JSON_VALUE)
    public String fallback(ServerWebExchange serverWebExchange) {
        LOGGER.warn("Circute Breaker");
        *//*LOGGER.info("ORIGIN: {}", headers.getOrigin());
        LOGGER.info("LOCATION: {}", headers.getLocation());*//*

        LOGGER.info("Query all webexchange attributes ------");
        for (Map.Entry<String, Object> entry : serverWebExchange.getAttributes().entrySet()) {
            LOGGER.info(""+entry.getKey()+" " + entry.getValue());
        }
        LOGGER.info("End Query all webexchange attributes -----");

        //Get the PathMatchInfo
        PathPattern.PathMatchInfo pathMatchInfo = serverWebExchange.getAttribute(ServerWebExchangeUtils.GATEWAY_ORIGINAL_REQUEST_URL_ATTR);

        //Get the template variables
        LOGGER.info("path match info ---" + pathMatchInfo.toString()+"----");
        Map<String, String> urlTemplateVariables = pathMatchInfo.getUriVariables();
        LOGGER.info("Yeah");
        for (Map.Entry<String, String> entry : urlTemplateVariables.entrySet()) {
            LOGGER.info(""+entry.getKey()+" " + entry.getValue());
        }
        LOGGER.info("NOPE");
        return redisService.getCache("/entitystorage/oem");
    }*/
}
