package run.halo.redisplugin;

import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisStreamCommands;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.HashMap;
import java.util.Map;

@Component
public class RedisMessagePublisher {

    private static final Logger logger = LoggerFactory.getLogger(RedisMessagePublisher.class);
    private RedisClient redisClient;
    private StatefulRedisConnection<String, String> connection;
    private RedisStreamCommands<String, String> streamCommands;

    @PostConstruct
    public void init() {
        redisClient = RedisClient.create("redis://localhost:6379");  // 默认连接到 localhost:6379
        connection = redisClient.connect();
        streamCommands = connection.sync();  // 获取同步命令接口
        logger.info("Lettuce Redis client initialized.");
    }

    @PreDestroy
    public void cleanup() {
        connection.close();
        redisClient.shutdown();
        logger.info("Lettuce Redis client connection closed.");
    }

    public void publish(String streamKey, String message) {
        Map<String, String> content = new HashMap<>();
        content.put("message", message);
        streamCommands.xadd(streamKey, content);
        logger.info("Published message to Redis stream {}: {}", streamKey, message);
    }
}
