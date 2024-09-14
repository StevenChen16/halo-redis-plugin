package com.stevenchen.redisplugin.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.data.redis.core.RedisTemplate;
import run.halo.app.event.comment.CommentNewEvent;  // 使用通用事件类
import run.halo.app.event.post.PostCreatedEvent;   // 使用通用事件类

import java.util.HashMap;
import java.util.Map;

@Component
public class HaloEventListener {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private final String STREAM_KEY = "halo-stream";

    @EventListener
    public void onPostCreated(PostCreatedEvent event) {
        publishMessage("POST_CREATED", event.getPostId());  // 使用现有方法获取文章ID
    }

    @EventListener
    public void onCommentAdded(CommentNewEvent event) {
        publishMessage("COMMENT_ADDED", event.getCommentId());  // 使用现有方法获取评论ID
    }

    private void publishMessage(String action, Long id) {
        Map<String, Object> message = new HashMap<>();
        message.put("action", action);
        message.put("id", id.toString());

        redisTemplate.opsForStream().add(STREAM_KEY, message);
    }
}
