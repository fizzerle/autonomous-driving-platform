package tuwien.dse.apigateway;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.GatewayFilterFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class InterceptorFilter implements GatewayFilterFactory<InterceptorFilter.Config> {

    private static final Logger LOGGER = LoggerFactory.getLogger(InterceptorFilter.class);

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            LOGGER.info("Request goes now to {}", exchange.getRequest().getURI());

            LOGGER.info("Response has http coe {}", exchange.getResponse().getStatusCode().value());

            return chain.filter(exchange);
        };
    }

    @Override
    public Config newConfig() {
        return new Config("InterceptorFilter");
    }

    public static class Config {
        private String name;

        public Config(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
