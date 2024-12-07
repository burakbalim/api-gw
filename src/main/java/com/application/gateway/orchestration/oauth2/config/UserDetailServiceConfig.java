package com.application.gateway.orchestration.oauth2.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

/**
 * Perform for custom password authentication type for in memory
 */
@Configuration
public class UserDetailServiceConfig {

    @Bean
    public UserDetailsService userDetailsService() {
        var user1 = User.withUsername("user")
                .password("password").
                authorities("read", "test")
                .build();
        return new InMemoryUserDetailsManager(user1);
    }
}
