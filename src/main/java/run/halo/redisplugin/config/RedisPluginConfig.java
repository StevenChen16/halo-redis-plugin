package com.stevenchen.redisplugin.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import run.halo.app.extension.ConfigurableOptionService;

@Component
public class RedisPluginConfig {

    @Data
    public static class BasicConfig {
        private String host;
        private int port;
        private String password;
    }

    private BasicConfig config;

    @Autowired
    public RedisPluginConfig(ConfigurableOptionService optionService) {
        BasicConfig config = new BasicConfig();
        config.setHost(optionService.get(RedisPluginOptions.HOST).orElse("localhost"));
        config.setPort(Integer.parseInt(optionService.get(RedisPluginOptions.PORT).orElse("6379")));
        config.setPassword(optionService.get(RedisPluginOptions.PASSWORD).orElse(""));
        this.config = config;
    }

    public BasicConfig getConfig() {
        return config;
    }
}
