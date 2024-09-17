package run.halo.redisplugin.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import run.halo.app.event.post.PostUpdatedEvent;
import run.halo.redisplugin.RedisMessagePublisher;

public class ContentChangeListener {

    private static final Logger logger = LoggerFactory.getLogger(ContentChangeListener.class);
    private final RedisMessagePublisher redisMessagePublisher;

    public ContentChangeListener(RedisMessagePublisher redisMessagePublisher) {
        this.redisMessagePublisher = redisMessagePublisher;
    }

    @EventListener
    public void handlePostUpdatedEvent(PostUpdatedEvent event) {
        logger.info("Received PostUpdatedEvent: {}", event);
        
        String postName = getPostNameFromEvent(event);
        logger.info("Post updated: {}", postName);

        log.debug("Publishing post updated event to Redis stream: {}", postName);

        // 通过Redis发送消息
        redisMessagePublisher.publish("halo-stream", postName);
    }

    private String getPostNameFromEvent(PostUpdatedEvent event) {
        // 假设 PostEvent 类有 getPostName() 方法
        try {
            return (String) event.getClass().getMethod("getPostName").invoke(event);
        } catch (NoSuchMethodException e) {
            logger.error("Method getPostName() not found in PostUpdatedEvent", e);
            return "unknown_post";
        } catch (Exception e) {
            logger.error("Error retrieving postName from PostUpdatedEvent", e);
            return "unknown_post";
        }
    }
}
