package com.application.gateway.orchestration;

import com.application.gateway.orchestration.common.dto.ConfigurationSourceDTO;

/**
 * Represents an interface for configuring objects based on configuration data and notifying listeners of configuration changes.
 *
 * @param <T> The type of configuration data extending ConfigurationBaseDTO.
 */
public interface Configurable<T extends ConfigurationBaseDTO> {

    /**
     * Configures the object using the provided configuration source data.
     *
     * @param configurationSourceDTO The configuration source containing data to configure the object.
     * @return The configured object of type T.
     */
    T configure(ConfigurationSourceDTO<T> configurationSourceDTO);

    /**
     * Notifies listeners of configuration changes.
     */
    void notifyConfigurationChange();
}

