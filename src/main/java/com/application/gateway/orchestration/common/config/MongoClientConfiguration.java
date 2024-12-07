package com.application.gateway.orchestration.common.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(value="configuration.provider", havingValue = "MONGO")
public class MongoClientConfiguration {

}
