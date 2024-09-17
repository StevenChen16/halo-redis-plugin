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
    private StreamListener<String, MapRecord<String, String, String>> listener;
    private Thread listenerThread;
    private volatile boolean running = true;

    public RedisMessageSubscriber(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.listener = this::handleMessage;
    }

    @PostConstruct
    public void start() {
        listenerThread = new Thread(() -> {
            while (running) {
                try {
                    // 监听 "post_updated" 流
                    redisTemplate.opsForStream().listen("post_updated", listener);
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
