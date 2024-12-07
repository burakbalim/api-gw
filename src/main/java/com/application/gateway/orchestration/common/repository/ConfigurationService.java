package com.application.gateway.orchestration.common.repository;

import com.application.gateway.orchestration.common.dto.ConfigurationSourceDTO;

/**
 * Service interface for reading configuration data.
 */
public interface ConfigurationService {

    /**
     * Reads configuration data based on the provided configuration source DTO.
     *
     * @param <T>                    The type of configuration data.
     * @param configurationSourceDTO The configuration source DTO containing information about the configuration data.
     * @return The read configuration data.
     */
    <T> T read(ConfigurationSourceDTO<T> configurationSourceDTO);
}
