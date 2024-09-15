package com.stevenchen.redisplugin.config;

import run.halo.app.extension.Option;

public enum RedisPluginOptions implements Option {
    HOST("redis.host", "localhost"),
    PORT("redis.port", "6379"),
    PASSWORD("redis.password", "");

    private final String key;
    private final String defaultValue;

    RedisPluginOptions(String key, String defaultValue) {
        this.key = key;
        this.defaultValue = defaultValue;
    }

    @Override
    public String key() {
        return key;
    }

    @Override
    public String defaultValue() {
        return defaultValue;
    }
}
