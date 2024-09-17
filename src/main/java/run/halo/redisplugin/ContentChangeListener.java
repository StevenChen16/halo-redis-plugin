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
        String postName = getPostNameFromEvent(event);
        logger.info("Post updated: {}", postName);

        // 通过Redis发送消息
        redisMessagePublisher.publish("post_updated", postName);
    }

    private String getPostNameFromEvent(PostUpdatedEvent event) {
        // 尝试通过反射获取 postName
        try {
            // 检查是否有 getPostName() 方法
            return (String) event.getClass().getMethod("getPostName").invoke(event);
        } catch (NoSuchMethodException e) {
            // 如果没有，尝试通过 getPost() 方法获取 Post 对象，再获取名称
            try {
                Object post = event.getClass().getMethod("getPost").invoke(event);
                return (String) post.getClass().getMethod("getName").invoke(post);
            } catch (Exception ex) {
                logger.error("无法获取 postName", ex);
                return "unknown_post";
            }
        } catch (Exception e) {
            logger.error("无法获取 postName", e);
            return "unknown_post";
        }
    }
}
