package com.application.gateway.orchestration.common.repository;

import com.application.gateway.orchestration.common.dto.ConfigurationSourceDTO;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@ConditionalOnProperty(value = "configuration.provider", havingValue = "POSTGRES")
public class PostgresConfigurationServiceImpl implements ConfigurationService {

    private final JdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper;

    public PostgresConfigurationServiceImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    }


    /**
     * Reads configuration data from PostgreSQL JSONB storage based on the provided configuration source DTO.
     *
     * @param <T>                    The type of configuration data.
     * @param configurationSourceDTO The configuration source DTO containing information about the configuration data.
     * @return The read configuration data.
     */
    @Override
    public <T> T read(ConfigurationSourceDTO<T> configurationSourceDTO) {
        String configKey = configurationSourceDTO.getConfigurationSource();

        try {
            String SELECT_SQL = "SELECT data FROM %s ORDER BY id LIMIT 1";
            List<Object> results = jdbcTemplate.queryForList(
                    String.format(SELECT_SQL, configKey),
                    Object.class
            );

            if (results.isEmpty()) {
                throw new RuntimeException("No configuration data found for key: " + configKey);
            }

            return objectMapper.readValue(results.get(0).toString(), configurationSourceDTO.getSourceClazz());
        } catch (EmptyResultDataAccessException e) {
            throw new RuntimeException("Configuration not found for key: " + configKey, e);
        } catch (Exception e) {
            throw new RuntimeException("Failed to read configuration data for key: " + configKey, e);
        }
    }
}
