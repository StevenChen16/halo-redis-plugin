package com.example.redissync;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StreamOperations;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Component
public class RedisMessagePublisher {

    private static final Logger logger = LoggerFactory.getLogger(RedisMessagePublisher.class);

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    public void publish(String streamKey, String message) {
        StreamOperations<String, Object, Object> streamOps = redisTemplate.opsForStream();
        Map<String, Object> content = new HashMap<>();
        content.put("message", message);
        streamOps.add(streamKey, content);
        logger.info("Published message to Redis stream {}: {}", streamKey, message);
    }
}
