package run.halo.redisplugin;

import org.springframework.context.annotation.Bean;
import run.halo.app.plugin.BasePlugin;
import run.halo.redisplugin.listener.ContentChangeListener;

public class RedisSyncPlugin extends BasePlugin {

    public void onStarted() {
        // 插件启动时的初始化操作
        System.out.println("RedisSyncPlugin has started.");
    }

    public void onStopped() {
        // 插件停止时的清理操作
        System.out.println("RedisSyncPlugin has stopped.");
    }

    @Bean
    public ContentChangeListener contentChangeListener() {
        return new ContentChangeListener(redisMessagePublisher());
    }

    @Bean
    public RedisMessagePublisher redisMessagePublisher() {
        return new RedisMessagePublisher();
    }
}
