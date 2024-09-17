package run.halo.redisplugin;

import org.springframework.context.annotation.Bean;
import run.halo.app.plugin.BasePlugin;

public class RedisSyncPlugin extends BasePlugin {

    @Override
    public void onStarted() {
        // 插件启动时的初始化操作
        System.out.println("RedisSyncPlugin has started.");
        redisMessageSubscriber().subscribe("post_updated");
    }

    @Override
    public void onStopped() {
        // 插件停止时的清理操作
        System.out.println("RedisSyncPlugin has stopped.");
    }

    @Bean
    public RedisMessageSubscriber redisMessageSubscriber() {
        return new RedisMessageSubscriber();
    }

    @Bean
    public RedisMessagePublisher redisMessagePublisher() {
        return new RedisMessagePublisher();
    }
}
