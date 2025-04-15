package com.application.gateway.common.cache.redis;


import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;

@Configuration
public class RedisConfiguration {

    @Bean
    @ConditionalOnProperty(value="cache.custom.redis.enabled", havingValue = "true")
    public JedisConnectionFactory jedisConnectionFactory() {
        return new JedisConnectionFactory();
    }
}
