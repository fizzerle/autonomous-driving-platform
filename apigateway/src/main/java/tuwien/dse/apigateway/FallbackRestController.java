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

    /**
     * Fallback for GET-Requests.
     *
     * Extracts the url from the requests and invokes the redisService to get a cached response.
     * @param serverWebExchange ServerWebExchange
     * @return Cached Response
     */
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


    /**
     * Fallback for POST-Requests, that responses with a 503 Service Unavailable error response.
     * @return Error response
     */
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
}
