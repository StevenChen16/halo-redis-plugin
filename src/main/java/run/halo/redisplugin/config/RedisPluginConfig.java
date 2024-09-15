package com.stevenchen.redisplugin.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import run.halo.app.extension.ConfigurableOptionService;

@Component
public class RedisPluginConfig {

    @Autowired
    private ConfigurableOptionService optionService;

    @Data
    public static class BasicConfig {
        private String host;
        private int port;
        private String password;
    }

    public BasicConfig getRedisConfig() {
        BasicConfig config = new BasicConfig();
        config.setHost(optionService.get("redis.host").orElse("localhost"));
        config.setPort(Integer.parseInt(optionService.get("redis.port").orElse("6379")));
        config.setPassword(optionService.get("redis.password").orElse(""));
        return config;
    }
}
