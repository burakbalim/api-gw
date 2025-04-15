package com.application.gateway.orchestration.common.config;

import com.application.gateway.orchestration.oauth2.model.ClientTypeConverter;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;

import static com.application.gateway.orchestration.common.util.Constants.CONFIG_DB;

@Configuration
@RequiredArgsConstructor
@ConditionalOnProperty(value="configuration.provider", havingValue = "MONGO")
public class MongoTemplateConfiguration extends AbstractMongoClientConfiguration {

    @Value("${spring.data.mongodb.uri}")
    private String connectionString;

    @Override
    public @Bean MongoClient mongoClient() {
        return MongoClients.create(connectionString);
    }

    @Bean
    public org.springframework.data.mongodb.core.MongoTemplate mongoTemplate(MongoClient mongoClient) {
        MongoDatabase database = mongoClient.getDatabase(CONFIG_DB);
        return new org.springframework.data.mongodb.core.MongoTemplate(mongoClient, database.getName());
    }

    @Override
    protected String getDatabaseName() {
        return CONFIG_DB;
    }

    @Override
    protected void configureConverters(MongoCustomConversions.MongoConverterConfigurationAdapter adapter) {
        adapter.registerConverter(new ClientTypeConverter());
    }
}


