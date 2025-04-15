package com.application.gateway.main.middleware.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;

@Getter
@Setter
public class MiddlewareRequestRule implements Serializable {

    @JsonProperty("custom_function_name")
    @Field("custom_function_name")
    private String customFunctionName;

}
