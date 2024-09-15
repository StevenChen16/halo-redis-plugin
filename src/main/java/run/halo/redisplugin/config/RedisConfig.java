package com.stevenchen.redisplugin.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.*;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration(proxyBeanMethods = false)
public class RedisConfig {

    @Autowired
    private RedisPluginConfig redisPluginConfig;

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        RedisPluginConfig.BasicConfig config = redisPluginConfig.getConfig();

        // 配置 Redis 连接参数
        RedisStandaloneConfiguration redisConfig = new RedisStandaloneConfiguration(config.getHost(), config.getPort());
        // 如果需要密码：
        if (config.getPassword() != null && !config.getPassword().isEmpty()) {
            redisConfig.setPassword(RedisPassword.of(config.getPassword()));
        }
        return new LettuceConnectionFactory(redisConfig);
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        // 设置序列化器
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory());
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new StringRedisSerializer());
        return template;
    }
}
