package com.application.gateway.common.util;

import com.application.gateway.common.properties.AsyncThreadProperties;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.*;

@AllArgsConstructor
@Slf4j
@Component
public class ExecutorsProvider {

    private final AsyncThreadProperties asyncThreadProperties;

    public ExecutorService getExecutors() {
        if (asyncThreadProperties.isEnabledVirtualThread()) {
            return Executors.newVirtualThreadPerTaskExecutor();
        } else {
            return new ThreadPoolExecutor(asyncThreadProperties.getCorePoolSize(), asyncThreadProperties.getMaxPoolSize(),
                    asyncThreadProperties.getKeepAliveTime(), asyncThreadProperties.getTimeUnit(), new LinkedBlockingQueue<>(100));
        }
    }
}
