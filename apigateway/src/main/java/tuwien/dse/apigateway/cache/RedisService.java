package tuwien.dse.apigateway.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;

@Service
public class RedisService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RedisService.class);

    private Jedis jedis;

    /**
     * Constructor for the RedisService which opens a connection to the Redis-Store used for caching.
     * @param host Address where the store is hosted
     * @param port Port where the store can be reached
     * @param password Password for the store
     */
    @Autowired
    public RedisService(@Value("${redis.host}") String host,@Value("${redis.port}") Integer port,@Value("${redis.password}") String password) {
        try {
            jedis = new Jedis(host, port);
            jedis.connect();
            jedis.auth(password);
        } catch (Exception e) { 
            LOGGER.error("Could not create Jedis Client", e);
        }
    }

    /**
     * Gets the cached response from the Redis-store.
     * @param url Key for the cached entries.
     * @return Response for the cached entry.
     */
    public String getCache(String url) {
        LOGGER.info("redis response ------------------- : {}", jedis.get(url));
        return jedis.get(url);
    }

}