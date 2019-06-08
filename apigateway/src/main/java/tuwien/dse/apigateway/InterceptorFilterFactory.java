package tuwien.dse.apigateway;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.GatewayFilterFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import tuwien.dse.apigateway.cache.HttpResponse;
import tuwien.dse.apigateway.cache.ResponseCache;
import tuwien.dse.apigateway.cache.ResponseCacheKey;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

@Component
public class InterceptorFilterFactory implements GatewayFilterFactory<InterceptorFilterFactory.Config> {

    private static final Logger LOGGER = LoggerFactory.getLogger(InterceptorFilterFactory.class);

    private ResponseCache cache;

    @Autowired
    public void setCache(ResponseCache cache) {
        this.cache = cache;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            String body = getBody(exchange);
            cache.put(new ResponseCacheKey(
                        exchange.getRequest().getURI().toString(),
                        exchange.getRequest().getMethodValue()
                    ), new HttpResponse(
                        body,
                        exchange.getResponse().getStatusCode().value()
                    ));

            LOGGER.info("Request goes now to {}", exchange.getRequest().getURI());

            LOGGER.info("Response has http code {} and body: {}", exchange.getResponse().getStatusCode().value(), body);

            return chain.filter(exchange);
        };
    }

    private String getBody(ServerWebExchange exchange) {
        InputStream is = exchange.getResponse().bufferFactory().wrap(new byte[Integer.MAX_VALUE]).asInputStream();
        try( BufferedReader br =
                     new BufferedReader( new InputStreamReader(is, "UTF-8" )))
        {
            StringBuilder sb = new StringBuilder();
            String line;
            while(( line = br.readLine()) != null ) {
                sb.append( line );
                sb.append( '\n' );
            }
            return sb.toString();
        } catch (IOException e) {
            return "";
        }
    }


    @Override
    public Config newConfig() {
        return new Config("InterceptorFilterFactory");
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
