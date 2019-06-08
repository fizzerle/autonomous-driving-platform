package tuwien.dse.apigateway;

import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.GatewayFilterFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuwien.dse.apigateway.cache.HttpResponse;
import tuwien.dse.apigateway.cache.ResponseCache;
import tuwien.dse.apigateway.cache.ResponseCacheKey;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
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

            LOGGER.info("Request goes now to {}", exchange.getRequest().getURI());

            ServerHttpResponse originalResponse = exchange.getResponse();
      		DataBufferFactory bufferFactory = originalResponse.bufferFactory();
            ServerHttpResponseDecorator decoratedResponse = new ServerHttpResponseDecorator(originalResponse) {
                 @Override
                 public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
                 	if (body instanceof Flux) {
                         Flux<? extends DataBuffer> fluxBody = (Flux<? extends DataBuffer>) body;
                         return super.writeWith(fluxBody.map(dataBuffer -> {
                         	// probably should reuse buffers
                         	byte[] content = new byte[dataBuffer.readableByteCount()];
                         	dataBuffer.read(content);
                         	String strBody = new String(content, Charset.forName("UTF-8"));
                             cache.put(new ResponseCacheKey(
                                     exchange.getRequest().getURI().toString(),
                                     exchange.getRequest().getMethodValue()
                             ), new HttpResponse(
                                     strBody,
                                     originalResponse.getStatusCode().value()
                             ));
                             LOGGER.info("Response has http code {} and body: {}", originalResponse.getStatusCode().value(), strBody);
                         	return bufferFactory.wrap(content);
                         }));
                    }
                 	return super.writeWith(body); // if body is not a flux. never got there.
                 }
            };

            return chain.filter(exchange.mutate().response(decoratedResponse).build()); // replace response with decorator
        };
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
