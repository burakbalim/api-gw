package com.application.gateway.main.middleware.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;

@Getter
@Setter
public class SingleMiddlewareConfigurationDTO implements Serializable {

    @JsonProperty("name")
    @Field("name")
    private String name;

    @JsonProperty("before_request")
    @Field("before_request")
    private MiddlewareRequestRule beforeRequest;

    @JsonProperty("after_request")
    @Field("after_request")
    private MiddlewareRequestRule afterRequest;

    @JsonProperty("extended_paths")
    @Field("extended_paths")
    private ExtendedPath extendedPath;
}
