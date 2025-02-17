package com.titanium.web.starter.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ConfigurableBootstrapContext;
import org.springframework.boot.SpringApplicationRunListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;

@Slf4j
@Component
public class ApplicationRunnerListener implements SpringApplicationRunListener {
    private static LocalDateTime start = null;

    @Override
    public void starting(ConfigurableBootstrapContext bootstrapContext) {
        SpringApplicationRunListener.super.starting(bootstrapContext);
        start = LocalDateTime.now();
    }

    @Override
    public void ready(ConfigurableApplicationContext context, Duration timeTaken) {
//        String applicationName = context.getApplicationName();
        String applicationName = context.getEnvironment().getProperty("spring.application.name");
        String port = context.getEnvironment().getProperty("server.port");
        long usedTime = Duration.between(start, LocalDateTime.now()).toSeconds();
        log.info("Application: {}", applicationName);
        log.info("Started on port: {}", port);
        log.info("Spend time: {} s", usedTime);
        SpringApplicationRunListener.super.ready(context, timeTaken);

    }
}
