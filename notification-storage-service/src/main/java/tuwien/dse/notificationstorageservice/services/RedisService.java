package tuwien.dse.notificationstorageservice.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    private ObjectMapper mapper = new ObjectMapper();

    @Autowired
    public RedisService(@Value("${redis.host}") String host, @Value("${redis.port}") Integer port, @Value("${redis.password}") String password) {
        try {
            jedis = new Jedis(host, port);
            jedis.connect();
            jedis.auth(password);
        } catch (Exception e) {
            LOGGER.error("Could not create Jedis Client", e);
        }
    }

    /**
     * Method to cache responses by saving them in the Redis-Store.
     * @param url Request url for which a response will be cached.
     * @param response Response for the cached Request.
     */
    public void cache(String url, Object response) {

        try {
            jedis.set(url, mapper.writeValueAsString(response));
        } catch (JsonProcessingException e) {
            LOGGER.warn("Error caching get response {}", url);
        }
    }

    public String getCache(String url) {
        LOGGER.info("redis response ------------------- : {}", jedis.get(url));
        return jedis.get(url);
    }

}