package com.lzp.smarthomesys.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync // 开启异步执行，添加到启动类或者配置上；
public class ThreadPoolConfig {

    /**
     * use: SpringBoot线程池配置
     * @return ThreadPoolTaskExecutor
     */
    @Bean("myThreadPool")  //线程池实例名，多个线程池配置需要声明，一个线程池可有可无
    public ThreadPoolTaskExecutor executor(){
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        //配置核心线程数, 核心线程数线程数定义了最小可以同时运行的线程数量
        executor.setCorePoolSize(15);
        //配置最大线程数, 当队列中存放的任务达到队列容量的时候，当前可以同时运行的线程数量变为最大线程数
        executor.setMaxPoolSize(30);
        //配置队列大小, 当新任务来的时候会先判断当前运行的线程数量是否达到核心线程数，如果达到的话，信任就会被存放在队列中
        executor.setQueueCapacity(100);
        //线程的名称前缀
        executor.setThreadNamePrefix("myThreadPool-");
        //线程活跃时间（秒）
        //executor.setKeepAliveSeconds(60);
        //等待所有任务结束后再关闭线程池
        executor.setWaitForTasksToCompleteOnShutdown(true);
        //设置拒绝策略
        //executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        //执行初始化
        executor.initialize();
        return executor;
    }
}
