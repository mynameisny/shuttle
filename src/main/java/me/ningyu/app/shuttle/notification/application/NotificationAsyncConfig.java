package me.ningyu.app.shuttle.notification.application;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * 通知异步执行配置：通知编排在业务事务提交后异步执行，与业务请求解耦。
 */
@Configuration
@EnableAsync
public class NotificationAsyncConfig
{
    @Bean(name = "notificationExecutor")
    public Executor notificationExecutor()
    {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(4);
        executor.setQueueCapacity(200);
        executor.setThreadNamePrefix("notification-");
        executor.initialize();
        return executor;
    }
}
