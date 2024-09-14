package com.stevenchen.redisplugin.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.context.ApplicationEvent;  // 使用通用事件类

import java.util.HashMap;
import java.util.Map;

@Component
public class HaloEventListener implements ApplicationListener<ApplicationEvent> {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private final String STREAM_KEY = "halo-stream";

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        // 打印事件类型，帮助确认触发的事件
        System.out.println("Received event: " + event.getClass().getSimpleName());

        // 检查事件的类型，并根据不同的事件类型执行操作
        if (event instanceof PostCreatedEvent) {
            PostCreatedEvent postEvent = (PostCreatedEvent) event;
            publishMessage("POST_CREATED", postEvent.getPostId());
        } else if (event instanceof CommentNewEvent) {
            CommentNewEvent commentEvent = (CommentNewEvent) event;
            publishMessage("COMMENT_ADDED", commentEvent.getCommentId());
        }
    }

    private void publishMessage(String action, Long id) {
        Map<String, Object> message = new HashMap<>();
        message.put("action", action);
        message.put("id", id.toString());

        redisTemplate.opsForStream().add(STREAM_KEY, message);
    }
}
