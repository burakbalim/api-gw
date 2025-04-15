package com.application.gateway.orchestration.common.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.application.gateway.common.util.ObjectUtils;
import com.application.gateway.orchestration.common.dto.ConfigurationSourceDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.io.File;
import java.lang.reflect.Type;
import java.util.Collection;

@Slf4j
@Service
@ConditionalOnProperty(value="configuration.provider", havingValue = "FILE")
public class FileConfigurationServiceImpl implements ConfigurationService {

    @Override
    public <T> T read(ConfigurationSourceDTO<T> configurationSourceDTO) {
        return loadFromFile(configurationSourceDTO);
    }

    private <T> T loadFromFile(ConfigurationSourceDTO<T> configurationSourceDTO) {
        log.info("Loading file configuration from {}", configurationSourceDTO.getConfigurationSource());
        if (Collection.class.isAssignableFrom(configurationSourceDTO.getSourceClazz())) {
            TypeReference<T> typeReference = new TypeReference<>() {
                @Override
                public Type getType() {
                    return configurationSourceDTO.getSourceClazz();
                }
            };
            return ObjectUtils.readValue(new File(configurationSourceDTO.getConfigurationSource()), typeReference);
        }
        return ObjectUtils.readValue(new File(configurationSourceDTO.getConfigurationSource()), configurationSourceDTO.getSourceClazz());
    }
}
