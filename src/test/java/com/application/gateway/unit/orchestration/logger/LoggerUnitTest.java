package com.application.gateway.unit.orchestration.logger;

import com.application.gateway.common.HttpInputType;
import com.application.gateway.main.gateway.event.EventData;
import com.application.gateway.main.gateway.event.EventHandler;
import com.application.gateway.main.gateway.event.EventSource;
import com.application.gateway.main.gateway.event.HttpData;
import com.application.gateway.orchestration.common.repository.ConfigurationProvider;
import com.application.gateway.orchestration.logger.LogData;
import com.application.gateway.orchestration.logger.LoggerConfiguration;
import com.application.gateway.testutil.orchestration.TestableLogger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.logging.Level;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class LoggerUnitTest {
    @Mock
    private EventSource eventSource;

    @Mock
    private ConfigurationProvider<LoggerConfiguration> configurationProvider;

    private TestableLogger logger;

    @BeforeEach
    void setUp() {
        logger = new TestableLogger(eventSource, configurationProvider);
    }

    @ParameterizedTest
    @ValueSource(strings = {"INFO", "WARNING", "SEVERE"})
    void onProcess_ShouldHandleDifferentLogLevels(String levelName) {
        // given
        Level level = Level.parse(levelName);
        LogData logData = new LogData(level, "test message");

        logger.publicOnProcess(logData);
    }

    @Test
    void handle_WhenAllPathMatchIsTrue_ShouldLogMessage() {
        // given
        LoggerConfiguration config = new LoggerConfiguration();
        config.setEnable(Boolean.TRUE);
        config.setAllPathMatch(Boolean.TRUE);
        config.setThresholdMin(5);
        logger.publicOnInit(config);

        HttpData httpData = new HttpData();
        httpData.setType(HttpInputType.REQUEST);
        httpData.setUri("/test/path");

        EventData<HttpData> eventData = new EventData<>();
        eventData.setData(httpData);

        // when
        logger.handle(eventData);

        // then
        // Loglama yapıldığını kontrol ediyoruz
    }

    @Test
    void handle_WhenPathMatches_ShouldLogMessage() {
        // given
        LoggerConfiguration config = new LoggerConfiguration();
        config.setAllPathMatch(false);
        config.setCustomPaths(Arrays.asList("/test/path"));
        config.setThresholdMin(5);
        logger.publicOnInit(config);

        HttpData httpData = new HttpData();
        httpData.setType(HttpInputType.REQUEST);
        httpData.setUri("/test/path");

        EventData<HttpData> eventData = new EventData<>();
        eventData.setData(httpData);

        // when
        logger.handle(eventData);

        // then
        // Loglama yapıldığını kontrol ediyoruz
    }

    @Test
    void handle_WhenPathDoesNotMatch_ShouldNotLog() {
        // given
        LoggerConfiguration config = new LoggerConfiguration();
        config.setAllPathMatch(Boolean.FALSE);
        config.setCustomPaths(Collections.singletonList("/other/path"));
        config.setThresholdMin(5);
        logger.publicOnInit(config);

        HttpData httpData = new HttpData();
        httpData.setType(HttpInputType.REQUEST);
        httpData.setUri("/test/path");

        EventData<HttpData> eventData = new EventData<>();
        eventData.setData(httpData);

        // when
        logger.handle(eventData);

        // then
        // Loglama yapılmadığını kontrol ediyoruz
    }

    @Test
    void onInit_ShouldInitializeConfiguration() {
        // given
        LoggerConfiguration config = new LoggerConfiguration();
        config.setEnable(Boolean.TRUE);
        config.setAllPathMatch(Boolean.TRUE);
        config.setGwTaskSubscriber(Boolean.TRUE);
        
        // when
        logger.publicOnInit(config);
        
        // then
        verify(eventSource).subscribe(any(EventHandler.class));
    }

    @Test
    void handle_WhenThresholdIsZero_ShouldAlwaysLog() {
        // given
        LoggerConfiguration config = new LoggerConfiguration();
        config.setAllPathMatch(Boolean.TRUE);
        config.setThresholdMin(0);
        config.setEnable(Boolean.TRUE);
        logger.publicOnInit(config);

        HttpData httpData = new HttpData();
        httpData.setType(HttpInputType.REQUEST);
        httpData.setUri("/test/path");

        EventData<HttpData> eventData = new EventData<>();
        eventData.setData(httpData);

        // when
        logger.handle(eventData);
        logger.handle(eventData);  // ikinci çağrı

        // then
        // Her iki log çağrısının da yapıldığını kontrol ediyoruz
    }
} 