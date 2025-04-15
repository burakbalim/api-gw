package com.application.gateway.integration.orchestration.logger;

import com.application.gateway.main.gateway.event.EventSource;
import com.application.gateway.orchestration.common.repository.ConfigurationProvider;
import com.application.gateway.orchestration.logger.Logger;
import com.application.gateway.orchestration.logger.LoggerConfiguration;
import com.application.gateway.testutil.orchestration.TestableLogger;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class LoggerIntegrationTest {
    @Autowired
    private EventSource eventSource;

    @Autowired
    private ConfigurationProvider<LoggerConfiguration> configurationProvider;

    private TestableLogger logger;

    @BeforeEach
    void setUp() {
        logger = new TestableLogger(eventSource, configurationProvider);
    }

    // Integration testler burada...
} 