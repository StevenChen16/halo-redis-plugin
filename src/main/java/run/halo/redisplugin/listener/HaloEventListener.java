package com.stevenchen.redisplugin.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.HashMap;
import java.util.Map;

@Component
public class HaloEventListener {

    private static final Logger logger = LoggerFactory.getLogger(HaloEventListener.class);

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private final String STREAM_KEY = "halo-stream";

    // 自定义的业务逻辑，可以通过WebConfig来触发
    public void handleRequest(String action, Long id) {
        Map<String, Object> message = new HashMap<>();
        message.put("action", action);
        message.put("id", id != null ? id.toString() : "unknown");

        logger.info("发布消息到Redis: action=" + action + ", id=" + id);
        redisTemplate.opsForStream().add(STREAM_KEY, message);
    }
}
