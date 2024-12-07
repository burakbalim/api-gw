package com.application.gateway.main.virtualendpoints.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.application.gateway.orchestration.ConfigurationBaseDTO;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Set;

@Getter
@Setter
public class VirtualEndpointDTO implements ConfigurationBaseDTO {

    @JsonProperty("enable")
    @Field("enable")
    private Boolean enable;

    @JsonProperty("name")
    @Field("name")
    private String name;

    @JsonProperty("path")
    @Field("path")
    private String path;

    @JsonProperty("middlewares")
    @Field("middlewares")
    private Set<String> middlewares;

    @JsonProperty("class_name")
    @Field("class_name")
    private String className;

    @JsonProperty("use_session")
    @Field("use_session")
    private Boolean useSession;

    @Override
    public Boolean getEnable() {
        return enable;
    }
}
