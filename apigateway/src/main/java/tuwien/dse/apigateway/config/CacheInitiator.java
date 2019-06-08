package tuwien.dse.apigateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tuwien.dse.apigateway.cache.ResponseCache;

@Configuration
public class CacheInitiator {

    public static final String CACHE_ENTITY = "entityStorageResponseCache";

    @Bean(CACHE_ENTITY)
    public ResponseCache initEntityStorageCache() {
        return new ResponseCache(50);
    }
}
