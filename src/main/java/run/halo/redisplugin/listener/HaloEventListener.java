package com.stevenchen.redisplugin.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import run.halo.app.event.comment.CommentNewEvent;
import run.halo.app.event.post.PostCreatedEvent;

import java.util.HashMap;
import java.util.Map;

@Component
public class PostCreatedEventListener implements ApplicationListener<PostCreatedEvent> {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private final String STREAM_KEY = "halo-stream";

    @Override
    public void onApplicationEvent(PostCreatedEvent event) {
        publishMessage("POST_CREATED", event.getPostId());
    }

    private void publishMessage(String action, Long id) {
        Map<String, Object> message = new HashMap<>();
        message.put("action", action);
        message.put("id", id.toString());

        redisTemplate.opsForStream().add(STREAM_KEY, message);
    }
}

@Component
public class CommentNewEventListener implements ApplicationListener<CommentNewEvent> {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private final String STREAM_KEY = "halo-stream";

    @Override
    public void onApplicationEvent(CommentNewEvent event) {
        publishMessage("COMMENT_ADDED", event.getCommentId());
    }

    private void publishMessage(String action, Long id) {
        Map<String, Object> message = new HashMap<>();
        message.put("action", action);
        message.put("id", id.toString());

        redisTemplate.opsForStream().add(STREAM_KEY, message);
    }
}
