package com.stevenchen.redisplugin.listener;

import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import run.halo.app.plugin.event.PluginEvent;

import java.util.HashMap;
import java.util.Map;

@Component
public class HaloEventListener implements ApplicationListener<PluginEvent> {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private final String STREAM_KEY = "halo-stream";

    @Override
    public void onApplicationEvent(PluginEvent event) {
        // 根据事件类型处理
        if ("PostCreateEvent".equals(event.getType())) {
            publishMessage("POST_CREATED", (Long) event.getSource());
        } else if ("CommentNewEvent".equals(event.getType())) {
            publishMessage("COMMENT_ADDED", (Long) event.getSource());
        }
    }

    private void publishMessage(String action, Long id) {
        Map<String, Object> message = new HashMap<>();
        message.put("action", action);
        message.put("id", id.toString());

        redisTemplate.opsForStream().add(STREAM_KEY, message);
    }
}
