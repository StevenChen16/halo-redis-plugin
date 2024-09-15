package com.stevenchen.redisplugin.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class HaloEventListener implements ApplicationListener<ApplicationEvent> {

    private static final Logger logger = LoggerFactory.getLogger(HaloEventListener.class);

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private final String STREAM_KEY = "halo-stream";

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        // 记录事件的类名，便于调试确认事件类型
        logger.info("Received event: " + event.getClass().getName());

        // 根据事件的实际类型进行处理
        if (event.getClass().getSimpleName().equals("PostCreatedEvent")) {
            Long postId = getPostIdFromEvent(event);
            if (postId != null) {
                logger.info("PostCreatedEvent detected, postId: " + postId);
                publishMessage("POST_CREATED", postId);
            } else {
                logger.warn("PostCreatedEvent detected, but postId is null.");
            }
        } else if (event.getClass().getSimpleName().equals("CommentNewEvent")) {
            Long commentId = getCommentIdFromEvent(event);
            if (commentId != null) {
                logger.info("CommentNewEvent detected, commentId: " + commentId);
                publishMessage("COMMENT_ADDED", commentId);
            } else {
                logger.warn("CommentNewEvent detected, but commentId is null.");
            }
        }
    }

    private void publishMessage(String action, Long id) {
        Map<String, Object> message = new HashMap<>();
        message.put("action", action);
        message.put("id", id.toString());

        // 添加调试日志，确保方法被调用
        logger.info("Publishing message to Redis: action=" + action + ", id=" + id);

        redisTemplate.opsForStream().add(STREAM_KEY, message);
    }

    private Long getPostIdFromEvent(ApplicationEvent event) {
        try {
            return (Long) event.getClass().getMethod("getPostId").invoke(event);
        } catch (Exception e) {
            logger.error("Error getting postId from event", e);
            return null;
        }
    }

    private Long getCommentIdFromEvent(ApplicationEvent event) {
        try {
            return (Long) event.getClass().getMethod("getCommentId").invoke(event);
        } catch (Exception e) {
            logger.error("Error getting commentId from event", e);
            return null;
        }
    }
}
