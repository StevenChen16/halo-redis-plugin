package com.stevenchen.redisplugin.config;

import com.stevenchen.redisplugin.listener.HaloEventListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Configuration(proxyBeanMethods = false)  // 添加 proxyBeanMethods = false
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private HaloEventListener haloEventListener;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new HandlerInterceptor() {

            public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
                String requestURI = request.getRequestURI();
                String method = request.getMethod();

                Long id = extractIdFromRequest(request);
                if (requestURI.contains("/posts") && method.equals("PUT")) {
                    if (requestURI.contains("/publish")) {
                        haloEventListener.handleRequest("POST_CREATED", id);
                    } else {
                        haloEventListener.handleRequest("POST_UPDATED", id);
                    }
                } else if (requestURI.contains("/posts") && method.equals("DELETE")) {
                    haloEventListener.handleRequest("POST_DELETED", id);
                } else if (requestURI.contains("/comments") && method.equals("POST")) {
                    haloEventListener.handleRequest("COMMENT_ADDED", id);
                } else if (requestURI.contains("/comments") && method.equals("DELETE")) {
                    haloEventListener.handleRequest("COMMENT_DELETED", id);
                }
                return true;
            }

            private Long extractIdFromRequest(HttpServletRequest request) {
                String[] uriParts = request.getRequestURI().split("/");
                try {
                    return Long.parseLong(uriParts[uriParts.length - 1]);
                } catch (NumberFormatException e) {
                    return null;
                }
            }
        }).addPathPatterns("/apis/content.halo.run/v1alpha1/posts/**", "/apis/content.halo.run/v1alpha1/comments/**");
    }
}
