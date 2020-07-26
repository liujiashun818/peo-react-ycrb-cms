package cn.people.one.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by wilson on 2018-11-22.
 */
//@Configuration
//@EnableScheduling
//public class TaskConfig implements SchedulingConfigurer {
//
//    @Bean
//    public Executor taskExecutor() {
//        return Executors.newScheduledThreadPool(10);
//    }
//
//    @Override
//    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
//        taskRegistrar.setScheduler(taskExecutor());
//    }
//
//}
