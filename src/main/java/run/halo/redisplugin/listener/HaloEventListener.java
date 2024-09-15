package com.stevenchen.redisplugin.listener;

import com.stevenchen.redisplugin.event.CustomPostCreatedEvent;
import com.stevenchen.redisplugin.event.CustomCommentNewEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class CustomEventListener {

    private static final Logger logger = LoggerFactory.getLogger(CustomEventListener.class);

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private final String STREAM_KEY = "halo-stream";

    @EventListener
    public void handleCustomPostCreatedEvent(CustomPostCreatedEvent event) {
        Long postId = event.getPostId();
        if (postId != null) {
            logger.info("CustomPostCreatedEvent detected, postId: " + postId);
            publishMessage("POST_CREATED", postId);
        } else {
            logger.warn("CustomPostCreatedEvent detected, but postId is null.");
        }
    }

    @EventListener
    public void handleCustomCommentNewEvent(CustomCommentNewEvent event) {
        Long commentId = event.getCommentId();
        if (commentId != null) {
            logger.info("CustomCommentNewEvent detected, commentId: " + commentId);
            publishMessage("COMMENT_ADDED", commentId);
        } else {
            logger.warn("CustomCommentNewEvent detected, but commentId is null.");
        }
    }

    // 你可以根据需要添加更多的事件处理方法

    private void publishMessage(String action, Long id) {
        Map<String, Object> message = new HashMap<>();
        message.put("action", action);
        message.put("id", id.toString());

        // 添加调试日志，确保方法被调用
        logger.info("Publishing message to Redis: action=" + action + ", id=" + id);

        redisTemplate.opsForStream().add(STREAM_KEY, message);
    }
}
