package com.stevenchen.redisplugin.subscriber;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.data.redis.connection.stream.*;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Executors;

@Component
public class RedisStreamSubscriber implements InitializingBean {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private RestTemplate restTemplate = new RestTemplate();

    private final String STREAM_KEY = "halo-stream";
    private final String GROUP_NAME = "halo-group";

    @Override
    public void afterPropertiesSet() {
        Executors.newSingleThreadExecutor().submit(this::subscribe);
    }

    private void subscribe() {
        String consumerName = UUID.randomUUID().toString();

        // 创建消费组（如果不存在）
        try {
            redisTemplate.opsForStream().createGroup(STREAM_KEY, ReadOffset.latest(), GROUP_NAME);
        } catch (Exception e) {
            // 消费组已存在
        }

        while (true) {
            try {
                List<MapRecord<String, Object, Object>> messages = redisTemplate.opsForStream()
                        .read(Consumer.from(GROUP_NAME, consumerName),
                                StreamReadOptions.empty().block(Duration.ofSeconds(2)),
                                StreamOffset.create(STREAM_KEY, ReadOffset.lastConsumed()));

                if (messages != null && !messages.isEmpty()) {
                    for (MapRecord<String, Object, Object> message : messages) {
                        handleMessage(message.getValue());
                        // 确认消息
                        redisTemplate.opsForStream().acknowledge(GROUP_NAME, message);
                    }
                }
            } catch (Exception e) {
                // 处理异常，防止线程终止
                e.printStackTrace();
            }
        }
    }

    private void handleMessage(Map<Object, Object> message) {
        String action = (String) message.get("action");
        Long id = Long.valueOf((String) message.get("id"));

        switch (action) {
            case "POST_CREATED":
                updatePostCache(id);
                break;
            case "COMMENT_ADDED":
                updateCommentCache(id);
                break;
            default:
                // 处理其他动作
                break;
        }
    }

    private void updatePostCache(Long postId) {
        // 使用 REST API 获取文章信息
        String url = "http://localhost:8090/api/posts/" + postId;
        Map<String, Object> post = restTemplate.getForObject(url, Map.class);
        if (post != null) {
            // 更新缓存逻辑
        }
    }

    private void updateCommentCache(Long commentId) {
        // 使用 REST API 获取评论信息
        String url = "http://localhost:8090/api/comments/" + commentId;
        Map<String, Object> comment = restTemplate.getForObject(url, Map.class);
        if (comment != null) {
            // 更新缓存逻辑
        }
    }
}
