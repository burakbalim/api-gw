package com.application.gateway.orchestration.common.util;

import com.application.gateway.common.properties.AsyncThreadProperties;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;

@Component
@RequiredArgsConstructor
public class AsyncThreadPool {

    private final AsyncThreadProperties asyncThreadProperties;

    private ThreadPoolExecutor threadPoolExecutor;

    @PostConstruct
    public void init() {
        threadPoolExecutor = new ThreadPoolExecutor(asyncThreadProperties.getCorePoolSize(), asyncThreadProperties.getMaxPoolSize(), asyncThreadProperties.getKeepAliveTime(), asyncThreadProperties.getTimeUnit(), new LinkedBlockingDeque<>());
    }

    public ThreadPoolExecutor getThreadPoolExecutor() {
        return threadPoolExecutor;
    }
}
