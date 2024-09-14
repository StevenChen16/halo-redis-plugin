package com.stevenchen.redisplugin.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.data.redis.core.RedisTemplate;

import run.halo.app.core.extension.content.Post;
import run.halo.app.core.extension.content.Comment;
import run.halo.app.core.extension.event.PostEvent;
import run.halo.app.core.extension.event.CommentEvent;

import java.util.HashMap;
import java.util.Map;

@Component
public class HaloEventListener {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private final String STREAM_KEY = "halo-stream";

    @EventListener
    public void onPostEvent(PostEvent event) {
        if (event.getType() == PostEvent.Type.CREATED) {
            Post post = event.getPost();
            publishMessage("POST_CREATED", post.getId());
        }
    }

    @EventListener
    public void onCommentEvent(CommentEvent event) {
        if (event.getType() == CommentEvent.Type.CREATED) {
            Comment comment = event.getComment();
            publishMessage("COMMENT_ADDED", comment.getId());
        }
    }

    private void publishMessage(String action, Long id) {
        Map<String, Object> message = new HashMap<>();
        message.put("action", action);
        message.put("id", id.toString());

        redisTemplate.opsForStream().add(STREAM_KEY, message);
    }
}
