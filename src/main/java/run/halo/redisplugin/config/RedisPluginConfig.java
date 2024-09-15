package com.stevenchen.redisplugin.config;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
public class RedisPluginConfig {

    @Data
    public static class BasicConfig {
        private String host = "localhost";  // 默认 Redis 主机
        private int port = 6379;            // 默认 Redis 端口
        private String password = "";       // 默认 Redis 密码
    }

    private BasicConfig config = new BasicConfig();

    public BasicConfig getConfig() {
        return config;
    }

    // 手动设置 Redis 的 host
    public void setHost(String host) {
        config.setHost(host);
    }

    // 手动设置 Redis 的 port
    public void setPort(int port) {
        config.setPort(port);
    }

    // 手动设置 Redis 的 password
    public void setPassword(String password) {
        config.setPassword(password);
    }
}
