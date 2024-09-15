package com.stevenchen.redisplugin.config;

import com.stevenchen.redisplugin.listener.HaloEventListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private HaloEventListener haloEventListener;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(haloEventListener)
                .addPathPatterns("/apis/content.halo.run/v1alpha1/posts/**", "/apis/content.halo.run/v1alpha1/comments/**");
    }
}
