package com.stevenchen.redisplugin.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import run.halo.app.event.comment.CommentNewEvent;
import run.halo.app.event.comment.CommentReplyEvent;
import run.halo.app.event.post.PostUpdatedEvent;

import java.util.HashMap;
import java.util.Map;

@Component
public class HaloEventListener {

    private static final Logger logger = LoggerFactory.getLogger(HaloEventListener.class);

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private final String STREAM_KEY = "halo-stream";

    @EventListener
    public void handleCommentNewEvent(CommentNewEvent event) {
        Long commentId = event.getCommentId();
        if (commentId != null) {
            logger.info("CommentNewEvent detected, commentId: " + commentId);
            publishMessage("COMMENT_ADDED", commentId);
        } else {
            logger.warn("CommentNewEvent detected, but commentId is null.");
        }
    }

    @EventListener
    public void handleCommentReplyEvent(CommentReplyEvent event) {
        Long commentId = event.getCommentId();
        if (commentId != null) {
            logger.info("CommentReplyEvent detected, commentId: " + commentId);
            publishMessage("COMMENT_REPLIED", commentId);
        } else {
            logger.warn("CommentReplyEvent detected, but commentId is null.");
        }
    }

    @EventListener
    public void handlePostUpdatedEvent(PostUpdatedEvent event) {
        Long postId = event.getPost().getId();
        if (postId != null) {
            logger.info("PostUpdatedEvent detected, postId: " + postId);
            publishMessage("POST_UPDATED", postId);
        } else {
            logger.warn("PostUpdatedEvent detected, but postId is null.");
        }
    }

    // 你可以根据需要添加更多的事件处理方法，例如 PostDeletedEvent, CommentDeletedEvent 等

    private void publishMessage(String action, Long id) {
        Map<String, Object> message = new HashMap<>();
        message.put("action", action);
        message.put("id", id.toString());

        // 添加调试日志，确保方法被调用
        logger.info("Publishing message to Redis: action=" + action + ", id=" + id);

        redisTemplate.opsForStream().add(STREAM_KEY, message);
    }
}
