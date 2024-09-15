package com.stevenchen.redisplugin.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.data.redis.core.RedisTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@Component
public class HaloEventListener implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(HaloEventListener.class);

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private final String STREAM_KEY = "halo-stream";

    // 拦截API请求
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestURI = request.getRequestURI();
        String method = request.getMethod();

        if (requestURI.contains("/posts") && method.equals("PUT")) {
            // 文章发布或修改
            Long postId = extractIdFromRequest(request);
            if (requestURI.contains("/publish")) {
                logger.info("拦截到文章发布请求，postId: " + postId);
                publishMessage("POST_CREATED", postId);
            } else {
                logger.info("拦截到文章修改请求，postId: " + postId);
                publishMessage("POST_UPDATED", postId);
            }
        } else if (requestURI.contains("/posts") && method.equals("DELETE")) {
            // 文章删除
            Long postId = extractIdFromRequest(request);
            logger.info("拦截到文章删除请求，postId: " + postId);
            publishMessage("POST_DELETED", postId);
        } else if (requestURI.contains("/comments") && method.equals("POST")) {
            // 评论发布
            Long commentId = extractIdFromRequest(request);
            logger.info("拦截到评论发布请求，commentId: " + commentId);
            publishMessage("COMMENT_ADDED", commentId);
        } else if (requestURI.contains("/comments") && method.equals("DELETE")) {
            // 评论删除
            Long commentId = extractIdFromRequest(request);
            logger.info("拦截到评论删除请求，commentId: " + commentId);
            publishMessage("COMMENT_DELETED", commentId);
        }

        return true;
    }

    // 提取ID
    private Long extractIdFromRequest(HttpServletRequest request) {
        String[] uriParts = request.getRequestURI().split("/");
        try {
            return Long.parseLong(uriParts[uriParts.length - 1]);
        } catch (NumberFormatException e) {
            logger.warn("无法从请求URI中提取ID: " + request.getRequestURI());
            return null;
        }
    }

    // 发布消息到Redis Stream
    private void publishMessage(String action, Long id) {
        Map<String, Object> message = new HashMap<>();
        message.put("action", action);
        message.put("id", id != null ? id.toString() : "unknown");

        // 调试日志
        logger.info("发布消息到Redis: action=" + action + ", id=" + id);

        redisTemplate.opsForStream().add(STREAM_KEY, message);
    }

    @Component
    public static class WebConfig implements WebMvcConfigurer {
        @Autowired
        private HaloEventListener haloEventListener;

        @Override
        public void addInterceptors(InterceptorRegistry registry) {
            registry.addInterceptor(haloEventListener).addPathPatterns("/apis/content.halo.run/v1alpha1/posts/**", "/apis/content.halo.run/v1alpha1/comments/**");
        }
    }
}
