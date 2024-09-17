package run.halo.redisplugin;

import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisStreamCommands;
import io.lettuce.core.StreamMessage;
import io.lettuce.core.XReadArgs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.List;

@Component
public class RedisMessageSubscriber {

    private static final Logger logger = LoggerFactory.getLogger(RedisMessageSubscriber.class);
    private RedisClient redisClient;
    private StatefulRedisConnection<String, String> connection;
    private RedisStreamCommands<String, String> streamCommands;

    @PostConstruct
    public void init() {
        redisClient = RedisClient.create("redis://localhost:6379");
        connection = redisClient.connect();
        streamCommands = connection.sync();
        logger.info("Lettuce Redis client initialized for subscriber.");
    }

    public void subscribe(String streamKey) {
        while (true) {
            List<StreamMessage<String, String>> messages = streamCommands.xreadgroup(
                "groupName", "consumerName", XReadArgs.StreamOffset.lastConsumed(streamKey));
            for (StreamMessage<String, String> message : messages) {
                logger.info("Received message from Redis stream {}: {}", streamKey, message);
            }
            try {
                Thread.sleep(1000);  // 等待一段时间再继续检查
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    @PreDestroy
    public void cleanup() {
        connection.close();
        redisClient.shutdown();
        logger.info("Lettuce Redis client connection closed for subscriber.");
    }
}
