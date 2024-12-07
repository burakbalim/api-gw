package com.application.gateway.main.custompaths.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;
import java.util.Set;

@Getter
@Setter
public class PathRules implements Serializable {

    @JsonProperty("middlewares")
    @Field("middlewares")
    private Set<String> middlewares;

    @JsonProperty("require_session")
    @Field("require_session")
    private boolean requireSession;
}
