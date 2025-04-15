package com.application.gateway.orchestration.common.repository;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.application.gateway.orchestration.common.dto.ConfigurationSourceDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;

/**
 * Service implementation for reading configurations from MongoDB.
 */
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(value = "configuration.provider", havingValue = "MONGO")
public class MongoConfigurationServiceImpl implements ConfigurationService {

    private final MongoTemplate mongoTemplate;

    /**
     * Reads configuration data from MongoDB based on the provided configuration source DTO.
     *
     * @param <T>                    The type of configuration data.
     * @param configurationSourceDTO The configuration source DTO containing information about the configuration data.
     * @return The read configuration data.
     */
    @Override
    public <T> T read(ConfigurationSourceDTO<T> configurationSourceDTO) {
        MappingMongoConverter mappingMongoConverter = (MappingMongoConverter) mongoTemplate.getConverter();
        mappingMongoConverter.setTypeMapper(new DefaultMongoTypeMapper(null));
        Query query = new Query();
        if (Collection.class.isAssignableFrom(configurationSourceDTO.getSourceClazz())) {
            Type superclassType = configurationSourceDTO.getSourceClazz().getGenericSuperclass();
            ParameterizedType parameterizedType = (ParameterizedType) superclassType;
            Type[] typeArguments = parameterizedType.getActualTypeArguments();
            Class<?> aClass = (Class<?>) typeArguments[0];
            List<?> objects = mongoTemplate.find(query, aClass, configurationSourceDTO.getConfigurationSource());
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
            return objectMapper.convertValue(objects, configurationSourceDTO.getSourceClazz());
        } else {
            return mongoTemplate.find(query, configurationSourceDTO.getSourceClazz(), configurationSourceDTO.getConfigurationSource()).get(0);
        }
    }
}
