package com.application.gateway.orchestration.logger;

import com.application.gateway.common.HttpInputType;
import com.application.gateway.common.util.ObjectUtils;
import com.application.gateway.common.util.PathUtils;
import com.application.gateway.main.gateway.event.EventData;
import com.application.gateway.main.gateway.event.EventHandlerBase;
import com.application.gateway.main.gateway.event.EventSource;
import com.application.gateway.main.gateway.event.HttpData;
import com.application.gateway.orchestration.base.LoggerProvider;
import com.application.gateway.orchestration.common.repository.ConfigurationProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;

@Slf4j
@Service
public class Logger extends EventHandlerBase<LoggerConfiguration, LogData> implements LoggerProvider {

    private LoggerConfiguration loggerConfiguration;

    private final Map<HttpInputType, LocalDateTime> lastTimeGwLogMap = new EnumMap<>(HttpInputType.class);

    protected Logger(EventSource eventSource, ConfigurationProvider<LoggerConfiguration> configurationProvider) {
        super(eventSource, configurationProvider);
    }

    @Override
    protected void onInit(LoggerConfiguration configData) {
        loggerConfiguration = configData;
        super.notifyToEventSource(loggerConfiguration.getGwTaskSubscriber());
    }

    @Override
    protected void onProcess(LogData logData) {
        if (logData.getLevel().equals(Level.INFO)) {
            log.info(logData.getText());
        } else if (logData.getLevel().equals(Level.SEVERE)) {
            log.error(logData.getText());
        } else if (logData.getLevel().equals(Level.WARNING)) {
            log.warn(logData.getText());
        }
    }

    @Override
    public void log(Level level, String text) {
        process(new LogData(level, text));
    }

    /**
     * Handle evenData to write in log
     * <p>
     * Write log according to threshold
     * @param eventData associated with EventSource
     * @see EventSource
     */
    @Override
    public <T> void handle(EventData<T> eventData) {
        HttpData httpData = (HttpData) eventData.getData();
        if (checkThreshold(httpData.getType())) {
            return;
        }
        if (Boolean.TRUE.equals(loggerConfiguration.getAllPathMatch())) {
            log(Level.INFO, ObjectUtils.writeValueAsString(httpData));
        }
        if (PathUtils.isPathMatch(loggerConfiguration.getCustomPaths(), httpData.getUri())) {
            log(Level.INFO, ObjectUtils.writeValueAsString(httpData));
        }
        lastTimeGwLogMap.put(httpData.getType(), LocalDateTime.now());
    }

    private boolean checkThreshold(HttpInputType httpInputType) {
        LocalDateTime localDateTime = lastTimeGwLogMap.get(httpInputType);
        if (Objects.isNull(localDateTime)) {
            return false;
        }
        return LocalDateTime.now().minusMinutes(loggerConfiguration.getThresholdMin()).isBefore(localDateTime);
    }
}
