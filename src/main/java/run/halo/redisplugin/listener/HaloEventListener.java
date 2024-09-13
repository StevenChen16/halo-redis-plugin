import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import run.halo.app.event.comment.CommentNewEvent;
import run.halo.app.event.post.PostCreatedEvent;

import java.util.HashMap;
import java.util.Map;


@Component
public class HaloEventListener {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private final String STREAM_KEY = "halo-stream";

    private void publishMessage(String action, Long id) {
        Map<String, Object> message = new HashMap<>();
        message.put("action", action);
        message.put("id", id.toString());

        redisTemplate.opsForStream().add(STREAM_KEY, message);
    }

    @EventListener
    public void onPostCreated(PostCreatedEvent event) {
        publishMessage("POST_CREATED", event.getPost().getId());
    }

    @EventListener
    public void onCommentAdded(CommentNewEvent event) {
        publishMessage("COMMENT_ADDED", event.getComment().getId());
    }
}
