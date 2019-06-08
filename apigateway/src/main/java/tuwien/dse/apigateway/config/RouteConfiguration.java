package tuwien.dse.apigateway.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.factory.HystrixGatewayFilterFactory;
import org.springframework.cloud.gateway.filter.factory.rewrite.RewriteFunction;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;
import tuwien.dse.apigateway.cache.HttpResponse;
import tuwien.dse.apigateway.cache.ResponseCache;
import tuwien.dse.apigateway.cache.ResponseCacheKey;

import java.util.function.Consumer;

@Configuration
public class RouteConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(RouteConfiguration.class);

    private ResponseCache cache;

    @Autowired
    public void setCache(ResponseCache cache) {
        this.cache = cache;
    }

    private Consumer<HystrixGatewayFilterFactory.Config> hystrix = h -> h.setName("fallbackcmd")
            .setFallbackUri("forward:/fallback");
    private RewriteFunction<String, String> responseCacher = ((exchange, body) -> {
        ResponseCacheKey key = new ResponseCacheKey(
                exchange.getRequest().getURI().toString(),
                exchange.getRequest().getMethodValue()
        );
        HttpResponse response = new HttpResponse(
                body,
                exchange.getResponse().getStatusCode().value()
        );
        LOGGER.info("Caching request url {} {} with response code {} and body {}", key.getHttpMethod(), key.getUrl(),
                response.getCode(), response.getContent());
        cache.put(key, response);

        return Mono.just(body);
    });

    @Bean
    public RouteLocator initRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("event-websockets", r -> r.path("/eventstorage/websocket")
                        .filters(filter ->
                                filter.rewritePath("/(?<path>.*)", "/$\\{path}"))
                        .uri("lb://event-storage"))
                .route("notification-websockets", r -> r.path("/notificationstorage/websocket")
                        .filters(filter ->
                                filter.rewritePath("/(?<path>.*)", "/$\\{path}"))
                        .uri("lb://notification-storage"))
                .route("entity-storage-route", r -> r.path("/entitystorage/**")
                    .filters(filter ->
                        filter.rewritePath("/(?<path>.*)", "/$\\{path}")
                        .modifyResponseBody(String.class, String.class, responseCacher)
                        .hystrix(hystrix))
                    .uri("lb://entity-storage"))
                .route("event-storage-rout", r -> r.path("/eventstorage/**")
                        .filters(filter ->
                                filter.rewritePath("/(?<path>.*)", "/$\\{path}")
                                        .modifyResponseBody(String.class, String.class, responseCacher)
                                        .hystrix(hystrix))
                        .uri("lb://event-storage"))
                .route("notification-storage-rout", r -> r.path("/notificationstorage/**")
                        .filters(filter ->
                                filter.rewritePath("/(?<path>.*)", "/$\\{path}")
                                        .modifyResponseBody(String.class, String.class, responseCacher)
                                        .hystrix(hystrix))
                        .uri("lb://notification-storage"))
                .build();
        // TODO: Add a origin-request-header to all requests
    }
}
