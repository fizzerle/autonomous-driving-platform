package tuwien.dse.entitystorageservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;

@Service
public class RedisService {

    //port: 6379
    //pw: Nh77bVjnXDWY
    //host: 10.156.0.5

    private static final Logger LOGGER = LoggerFactory.getLogger(RedisService.class);

    private Jedis jedis;
    private ObjectMapper mapper = new ObjectMapper();

    public RedisService() {
        jedis = new Jedis("10.156.0.5", 6379);

    }

    public void cache(String url, Object response) {

        try {
            jedis.set(url, mapper.writeValueAsString(response));
        } catch (JsonProcessingException e) {
            LOGGER.warn("Error caching get response {}", url);
        }

    }

}