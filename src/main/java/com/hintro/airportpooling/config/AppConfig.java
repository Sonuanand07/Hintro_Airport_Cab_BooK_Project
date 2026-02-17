package com.hintro.airportpooling.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableConfigurationProperties(AppProperties.class)
public class AppConfig {

    @Bean(name = "matchingExecutor")
    public Executor matchingExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(8);
        executor.setMaxPoolSize(32);
        executor.setQueueCapacity(1000);
        executor.setThreadNamePrefix("matching-");
        executor.initialize();
        return executor;
    }
}
