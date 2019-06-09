package tuwien.dse.eventstorageservice.services;

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
    public void cache(String url, Object response) {

        try {
            jedis.set(url, mapper.writeValueAsString(response));
        } catch (JsonProcessingException e) {
            LOGGER.warn("Error caching get response {}", url);
        }

    }

}