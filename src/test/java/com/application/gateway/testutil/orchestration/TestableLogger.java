package com.application.gateway.testutil.orchestration;

import com.application.gateway.main.gateway.event.EventSource;
import com.application.gateway.orchestration.common.repository.ConfigurationProvider;
import com.application.gateway.orchestration.logger.LogData;
import com.application.gateway.orchestration.logger.Logger;
import com.application.gateway.orchestration.logger.LoggerConfiguration;

public class TestableLogger extends Logger {
    
    public TestableLogger(EventSource eventSource, 
                         ConfigurationProvider<LoggerConfiguration> configurationProvider) {
        super(eventSource, configurationProvider);
    }

    public void publicOnProcess(LogData logData) {
        super.onProcess(logData);
    }

    public void publicOnInit(LoggerConfiguration config) {
        super.onInit(config);
    }
} 