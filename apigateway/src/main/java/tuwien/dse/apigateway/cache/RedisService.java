package tuwien.dse.apigateway.cache;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;

@Service
@SpringBootConfiguration
public class RedisService {

    //port: 6379
    //pw: Nh77bVjnXDWY
    //host: 10.156.0.5

    private static final Logger LOGGER = LoggerFactory.getLogger(RedisService.class);

    private Jedis jedis;
    private ObjectMapper mapper = new ObjectMapper();

    @Value("${redis.host}")
    private String host;

    @Value("${redis.port}")
    private Integer port;

    @Value("${redis.password}")
    private String password;

    public RedisService() {
        try {
            LOGGER.error("host {} ----------", host);
            LOGGER.error("port {} ----------", host);
            jedis = new Jedis(host, port);
            jedis.connect();
            jedis.auth(password);
        } catch (Exception e) {
            LOGGER.error("Could not create Jedis Client", e);
        }
    }

    public String getCache(String url) {
        LOGGER.info("redis response ------------------- : {}", jedis.get(url));
        return jedis.get(url);
    }

}