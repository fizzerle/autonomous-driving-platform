package tuwien.dse.notificationstorageservice.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;

@Service
public class RedisService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RedisService.class);

    private Jedis jedis;
    private ObjectMapper mapper = new ObjectMapper();

    public RedisService() {
        try {
            jedis = new Jedis("10.156.0.5", 6379);
            jedis.connect();
            jedis.auth("Nh77bVjnXDWY");
        } catch (Exception e) {
            LOGGER.error("Could not create Jedis Client", e);
        }
    }

    public void cache(String url, Object response) {

        try {
            jedis.set(url, mapper.writeValueAsString(response));
        } catch (JsonProcessingException e) {
            LOGGER.warn("Error caching get response {}", url);
        }

    }

}