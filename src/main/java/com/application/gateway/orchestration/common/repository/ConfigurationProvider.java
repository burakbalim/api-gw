package com.application.gateway.orchestration.common.repository;

import com.application.gateway.orchestration.ConfigurationBaseDTO;
import com.application.gateway.orchestration.common.dto.ConfigurationSourceDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Service class responsible for providing configuration data.
 *
 * @param <T> The type of configuration data extending ConfigurationBaseDTO.
 */
@Service
@RequiredArgsConstructor
public class ConfigurationProvider<T extends ConfigurationBaseDTO> {

    private final ConfigurationService configurationService;

    /**
     * Loads configuration data based on the provided configuration source DTO.
     *
     * @param configurationSourceDTO The configuration source DTO containing information about the configuration data.
     * @return The loaded configuration data.
     */
    public T loadConfiguration(ConfigurationSourceDTO<T> configurationSourceDTO) {
        return configurationService.read(configurationSourceDTO);
    }
}
