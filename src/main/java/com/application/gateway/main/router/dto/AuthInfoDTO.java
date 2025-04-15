package com.application.gateway.main.router.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;

@Getter
@Setter
public class AuthInfoDTO implements Serializable {

    @JsonProperty("enable")
    @Field("enable")
    private Boolean enable;

    @JsonProperty("excluded_path")
    @Field("excluded_path")
    private String[] excludedPaths;
}
