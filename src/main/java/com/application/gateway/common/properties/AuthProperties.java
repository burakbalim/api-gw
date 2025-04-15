package com.application.gateway.common.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
public class AuthProperties {

    @Value("${gw-authorization}")
    private String gwAuthorization;
}
