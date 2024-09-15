package com.stevenchen.redisplugin.listener;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import run.halo.app.event.comment.CommentNewEvent;
import run.halo.app.event.post.PostCreatedEvent;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.HashMap;
import java.util.Map;

@Component
public class HaloEventListener {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private final String STREAM_KEY = "halo-stream";

    @EventListener
    public void handlePostCreatedEvent(PostCreatedEvent event) {
        Long postId = event.getPostId();
        publishMessage("POST_CREATED", postId);
    }

    @EventListener
    public void handleCommentNewEvent(CommentNewEvent event) {
        Long commentId = event.getCommentId();
        publishMessage("COMMENT_ADDED", commentId);
    }

    private void publishMessage(String action, Long id) {
        Map<String, Object> message = new HashMap<>();
        message.put("action", action);
        message.put("id", id.toString());

        // 添加调试日志，确保方法被调用
        System.out.println("Publishing message to Redis: action=" + action + ", id=" + id);

        redisTemplate.opsForStream().add(STREAM_KEY, message);
    }
}
