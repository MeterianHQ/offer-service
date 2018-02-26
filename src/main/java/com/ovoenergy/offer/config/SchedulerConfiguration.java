package com.ovoenergy.offer.config;

import com.ovoenergy.offer.task.BaseTaskExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.CronTask;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronTrigger;

import java.util.TimeZone;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
@EnableScheduling
public class SchedulerConfiguration implements SchedulingConfigurer {

    @Value("${scheduler.crone.expression}")
    private String croneExpression;

    @Value("${scheduler.crone.time.zone}")
    private String croneTimeZone;

    @Value("${scheduler.thread.pool.size.core}")
    private Integer corePoolSize;

    @Autowired
    @Qualifier("offerStatusUpdateTaskExecutor")
    private BaseTaskExecutor offerStatusUpdateTaskExecutor;

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.setScheduler(taskScheduler());
        taskRegistrar.addCronTask(offerStatusUpdateSchedulingTask());
    }

    @Bean(destroyMethod = "shutdown")
    public ExecutorService taskScheduler() {
        return Executors.newScheduledThreadPool(corePoolSize);
    }

    @Bean
    public CronTask offerStatusUpdateSchedulingTask() {
        return new CronTask(offerStatusUpdateTaskExecutor, cronTrigger());
    }

    @Bean
    CronTrigger cronTrigger() {
        return new CronTrigger(croneExpression, TimeZone.getTimeZone(croneTimeZone));
    }
}
