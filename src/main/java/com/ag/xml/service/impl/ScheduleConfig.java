package com.ag.xml.service.impl;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import java.util.concurrent.Executors;

// 所有的定时任务都放在一个线程池中，定时任务启动时使用不同都线程。
@Configuration
public class ScheduleConfig implements SchedulingConfigurer
{
    @Value("${thread.pool}")
    private Integer threadPool;

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar)
    {
        // 设定一个长度10的定时任务线程池
        taskRegistrar.setScheduler(Executors.newScheduledThreadPool(threadPool));
    }
}