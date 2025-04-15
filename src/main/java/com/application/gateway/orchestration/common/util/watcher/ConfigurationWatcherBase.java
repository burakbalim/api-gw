package com.application.gateway.orchestration.common.util.watcher;

import com.application.gateway.orchestration.Configurable;
import com.application.gateway.orchestration.ConfigurationBaseDTO;
import com.application.gateway.orchestration.common.dto.ConfigurationSourceDTO;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public abstract class ConfigurationWatcherBase implements ConfigWatcher {

    private ExecutorService sourceWatcherExecutorService;

    protected final Map<ConfigurationSourceDTO<?>, Configurable<?>> pathToServiceMap = new HashMap<>();

    protected abstract void onListen();

    protected abstract void onInit();

    @PostConstruct
    @Override
    public void init() {
        sourceWatcherExecutorService = Executors.newSingleThreadExecutor(r -> new Thread(null, r, "ConfigurationWatcher", 0, false));
    }

    protected void notifyToSubscriber(String configurationSourceName) {
        try {
            Configurable<?> configurable = pathToServiceMap.get(new ConfigurationSourceDTO<>(configurationSourceName));
            if (Objects.isNull(configurable)) {
                log.error("Configurable is null for {}", configurationSourceName);
            } else {
                configurable.notifyConfigurationChange();
            }
        } catch (Exception e) {
            log.error("", e);
        }
    }

    @Override
    public <T extends ConfigurationBaseDTO> void subscribe(ConfigurationSourceDTO<T> configurationSourceDTO, Configurable<T> orchestrationService) {
        pathToServiceMap.put(configurationSourceDTO, orchestrationService);
    }

    @Override
    public void start() {
        this.onInit();
        sourceWatcherExecutorService.execute(this::onListen);
    }
}
