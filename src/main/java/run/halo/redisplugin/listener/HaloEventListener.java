package com.stevenchen.redisplugin.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class HaloEventListener implements ApplicationListener<ApplicationEvent> {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private final String STREAM_KEY = "halo-stream";

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        // 打印事件的类名，便于调试确认事件类型
        System.out.println("Received event: " + event.getClass().getName());

        // 根据事件的实际类型进行处理
        if (event.getClass().getSimpleName().equals("PostCreatedEvent")) {
            // 假设事件有 getPostId() 方法
            Long postId = getPostIdFromEvent(event);
            publishMessage("POST_CREATED", postId);
        } else if (event.getClass().getSimpleName().equals("CommentNewEvent")) {
            // 假设事件有 getCommentId() 方法
            Long commentId = getCommentIdFromEvent(event);
            publishMessage("COMMENT_ADDED", commentId);
        }
    }

    private void publishMessage(String action, Long id) {
        Map<String, Object> message = new HashMap<>();
        message.put("action", action);
        message.put("id", id.toString());

        // 添加调试日志，确保方法被调用
        System.out.println("Publishing message to Redis: action=" + action + ", id=" + id);

        redisTemplate.opsForStream().add(STREAM_KEY, message);
    }

    private Long getPostIdFromEvent(ApplicationEvent event) {
        try {
            return (Long) event.getClass().getMethod("getPostId").invoke(event);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private Long getCommentIdFromEvent(ApplicationEvent event) {
        try {
            return (Long) event.getClass().getMethod("getCommentId").invoke(event);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
