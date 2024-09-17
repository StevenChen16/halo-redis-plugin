package com.example.redissync.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import run.halo.app.event.post.PostUpdatedEvent;

public class ContentChangeListener {

    private static final Logger logger = LoggerFactory.getLogger(ContentChangeListener.class);
    private final RedisMessagePublisher redisMessagePublisher;

    public ContentChangeListener(RedisMessagePublisher redisMessagePublisher) {
        this.redisMessagePublisher = redisMessagePublisher;
    }

    @EventListener
    public void handlePostUpdatedEvent(PostUpdatedEvent event) {
        String postName = event.getPostName();
        logger.info("Post updated: {}", postName);

        // 通过Redis发送消息
        redisMessagePublisher.publish("post_updated", postName);
    }
}
