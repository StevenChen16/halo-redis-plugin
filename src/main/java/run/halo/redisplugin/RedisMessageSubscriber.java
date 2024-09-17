package run.halo.redisplugin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Component
public class RedisMessageSubscriber {

    private static final Logger logger = LoggerFactory.getLogger(RedisMessageSubscriber.class);
    private final RedisTemplate<String, String> redisTemplate;
    private final StreamListener<String, MapRecord<String, String, String>> listener;
    private Thread listenerThread;

    public RedisMessageSubscriber(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.listener = this::handleMessage;
    }

    @PostConstruct
    public void start() {
        listenerThread = new Thread(() -> {
            redisTemplate.opsForStream().listen("post_updated", listener);
        });
        listenerThread.start();
        logger.info("RedisMessageSubscriber started.");
    }

    @PreDestroy
    public void stop() {
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
