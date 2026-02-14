package com.gucardev.eventsphere.infrastructure.config.scheduler;

import com.gucardev.eventsphere.infrastructure.exception.ExceptionLogger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.util.ErrorHandler;

@Slf4j
@Configuration
public class SchedulerConfiguration implements SchedulingConfigurer {

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.setScheduler(taskScheduler());
    }

    @Bean
    public ThreadPoolTaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(2);
        scheduler.setThreadNamePrefix("Scheduler-");
        scheduler.setErrorHandler(schedulerErrorHandler());
        scheduler.initialize();
        return scheduler;
    }

    @Bean
    public ErrorHandler schedulerErrorHandler() {
        return throwable -> ExceptionLogger.logError(log, "SCHEDULER", throwable);
    }
}