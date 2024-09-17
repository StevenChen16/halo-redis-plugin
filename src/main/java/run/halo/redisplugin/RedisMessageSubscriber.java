package run.halo.redisplugin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.stream.*;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.time.Duration;
import java.util.List;

@Component
public class RedisMessageSubscriber {

    private static final Logger logger = LoggerFactory.getLogger(RedisMessageSubscriber.class);
    private final RedisTemplate<String, String> redisTemplate;
    private Thread listenerThread;
    private volatile boolean running = true;
    private static final String STREAM_KEY = "post_updated";
    private static final String GROUP_NAME = "halo_group";
    private static final String CONSUMER_NAME = "halo_consumer";

    public RedisMessageSubscriber(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @PostConstruct
    public void start() {
        // 创建消费者组，如果已存在则忽略异常
        try {
            redisTemplate.opsForStream().createGroup(STREAM_KEY, GroupCreateOptions.noStream().groupName(GROUP_NAME));
            logger.info("Created Redis stream group '{}'", GROUP_NAME);
        } catch (Exception e) {
            logger.warn("Redis stream group '{}' might already exist.", GROUP_NAME);
        }

        listenerThread = new Thread(() -> {
            while (running) {
                try {
                    ReadOffset readOffset = ReadOffset.lastConsumed();
                    Consumer consumer = Consumer.from(GROUP_NAME, CONSUMER_NAME);
                    StreamReadOptions options = StreamReadOptions.empty().count(1).block(Duration.ofSeconds(5));
                    List<MapRecord<String, String, String>> messages = redisTemplate.opsForStream()
                            .read(consumer, options, StreamOffset.create(STREAM_KEY, readOffset));

                    if (messages != null && !messages.isEmpty()) {
                        for (MapRecord<String, String, String> message : messages) {
                            handleMessage(message);
                        }
                    }
                } catch (Exception e) {
                    logger.error("Error while listening to Redis stream", e);
                    try {
                        Thread.sleep(1000); // 等待一秒后重试
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        });
        listenerThread.start();
        logger.info("RedisMessageSubscriber started.");
    }

    @PreDestroy
    public void stop() {
        running = false;
        if (listenerThread != null && listenerThread.isAlive()) {
            listenerThread.interrupt();
        }
        logger.info("RedisMessageSubscriber stopped.");
    }

    private void handleMessage(MapRecord<String, String, String> record) {
        String message = record.getValue().get("message");
        logger.info("Received message from Redis stream {}: {}", record.getStream(), message);
        // 在这里执行同步操作
        // 例如：检查数据库并更新内存索引
    }
}
