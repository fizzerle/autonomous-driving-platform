package tuwien.dse.apigateway.config;

import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import java.net.URI;
import java.util.LinkedHashSet;

@Configuration
public class OriginRequestHeaderFilter {

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public GlobalFilter initOriginRequestHeaderFilter() {
        return (exchange, chain) -> {
            LinkedHashSet<URI> uris = exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_ORIGINAL_REQUEST_URL_ATTR);
            URI uri = uris.stream().findFirst().orElseGet(null);
            if (uri != null) {
                exchange.getRequest().mutate().header("X-Origin", uri.getPath()).build();
            }
            return chain.filter(exchange).then();
        };
    }
}
