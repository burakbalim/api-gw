package com.application.gateway.orchestration;

import com.application.gateway.common.util.ObjectUtils;
import com.application.gateway.orchestration.common.dto.ConfigurationSourceDTO;
import com.application.gateway.orchestration.common.repository.ConfigurationProvider;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

/**
 * Abstract base class for objects that can be configured using a configuration provider and can notify subscribers about configuration changes.
 *
 * @param <T> The type of configuration data extending ConfigurationBaseDTO.
 */
@Slf4j
public abstract class ConfigurableBase<T extends ConfigurationBaseDTO> implements Configurable<T> {

    private final ConfigurationProvider<T> configurationProvider;

    private T configuration;

    /**
     * Constructs a ConfigurableBase object with the provided configuration provider.
     *
     * @param configurationProvider The configuration provider used to obtain configuration data.
     */
    protected ConfigurableBase(ConfigurationProvider<T> configurationProvider) {
        this.configurationProvider = configurationProvider;
    }

    /**
     * Method to be implemented by subclasses to notify subscribers about configuration changes.
     */
    protected abstract void onNotifyConfigurationChange();

    @Override
    public T configure(ConfigurationSourceDTO<T> configurationSourceDTO) {
        configuration = configurationProvider.loadConfiguration(configurationSourceDTO);
        return configuration;
    }

    @Override
    public void notifyConfigurationChange() {
        log.info("Configuration changing is detected.");

        log.info("Old configuration:\n{}", ObjectUtils.writeValueAsString(configuration));

        onNotifyConfigurationChange();

        log.info("New configuration:\n{}", ObjectUtils.writeValueAsString(configuration));
    }

    /**
     * Retrieves the configured file and handles any exceptions that occur during configuration parsing.
     *
     * @param configurationSourceDTO The configuration source DTO.
     * @return The configured object.
     */
    protected T getConfiguredFile(ConfigurationSourceDTO<T> configurationSourceDTO) {
        try {
            return configure(configurationSourceDTO);
        } catch (Exception e) {
            log.error("Occurred exception while parsing configured file for service: " + getClass().getName(), e);
            assert Objects.nonNull(configuration);
            return configuration;
        }
    }
}
