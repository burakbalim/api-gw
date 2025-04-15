package com.application.gateway.orchestration;

import com.application.gateway.orchestration.common.dto.ConfigurationSourceDTO;

/**
 * Interface for orchestrating services with lifecycle methods such as initialization, starting, processing, and stopping.
 *
 * @param <T> The type of configuration data extending ConfigurationBaseDTO.
 * @param <K> The type of data to be processed by the orchestration service.
 */
public interface OrchestrationService<T extends ConfigurationBaseDTO, K> {

    /**
     * Initializes the orchestration service with the provided configuration source DTO.
     *
     * @param configurationSourceDTO The configuration source DTO containing information about the configuration data.
     */
    void init(ConfigurationSourceDTO<T> configurationSourceDTO);

    /**
     * Starts the orchestration service.
     */
    void start();

    /**
     * Processes the provided data.
     *
     * @param k The data to be processed.
     */
    void process(K k);

    /**
     * Stops the orchestration service.
     */
    void stop();
}
