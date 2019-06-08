package tuwien.dse.apigateway.cache;

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
        try {
            jedis = new Jedis("10.156.0.5", 6379);
            jedis.connect();
            jedis.auth("Nh77bVjnXDWY");
        } catch (Exception e) {
            LOGGER.error("Could not create Jedis Client", e);
        }
    }

    public String getCache(String url) {
        LOGGER.info("redis response ------------------- : {}", jedis.get(url));
        return jedis.get(url);
    }

}