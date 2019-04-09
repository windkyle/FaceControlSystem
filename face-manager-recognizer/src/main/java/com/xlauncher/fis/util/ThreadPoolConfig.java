package com.xlauncher.fis.util;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author :baisl
 * @Email :baishuailei@xlauncher.io
 * @Date :2018/12/13 0013
 * @Desc :人脸识别分析模型计算线程
 **/
@Configuration
@EnableAsync
public class ThreadPoolConfig {
    @Bean
    public Executor asyncServiceExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // 核心线程数
        executor.setCorePoolSize(2);
        // 最大线程数
        executor.setMaxPoolSize(2);
        // 队列大小
        executor.setQueueCapacity(20);
        // 线程名字前缀
        executor.setThreadNamePrefix("Async.Executor.Service-");
        // setRejectedExecutionHandler：当pool已经达到max size的时候，如何处理新任务
        // CallerRunsPolicy：不在新线程中执行任务，而是由调用者所在的线程来执行
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 执行初始化
        executor.initialize();
        return executor;
    }

}
