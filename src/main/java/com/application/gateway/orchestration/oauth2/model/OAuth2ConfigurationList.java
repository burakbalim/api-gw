package com.application.gateway.orchestration.oauth2.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.application.gateway.orchestration.ConfigurationBaseDTO;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Getter
@Setter
public class OAuth2ConfigurationList implements ConfigurationBaseDTO {

    @JsonProperty("path_configurations")
    @Field("path_configurations")
    private List<PathConfiguration> pathConfigurations;

    @JsonProperty("configurations")
    @Field("configurations")
    private List<ClientConfiguration> configurations;

}
