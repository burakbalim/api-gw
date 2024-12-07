package com.application.gateway.main.middleware.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Field;


@Getter
@Setter
public class UrlRewrites {

    @JsonProperty("path")
    @Field("path")
    private String path;

    @JsonProperty("method")
    @Field("method")
    private String method;

    @JsonProperty("match_pattern")
    @Field("match_pattern")
    private String matchPattern;

    @JsonProperty("rewrite_to")
    @Field("rewrite_to")
    private String rewriteTo;
}
