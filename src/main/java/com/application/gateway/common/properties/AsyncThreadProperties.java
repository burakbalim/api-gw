package com.application.gateway.common.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
@Getter
@Setter
public class AsyncThreadProperties {

    @Value("${async.virtual.thread}")
    private boolean isEnabledVirtualThread;

    @Value("${async.core.pool.size}")
    private int corePoolSize;

    @Value("${async.max.pool.size}")
    private int maxPoolSize;

    @Value("${async.keep.alive.time}")
    private Long keepAliveTime;

    @Value("${async.time.unit}")
    private TimeUnit timeUnit;

}
