package com.application.gateway.main.middleware.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;
import java.util.Map;

@Getter
@Setter
public class TransformHeader {

    @JsonProperty("path")
    @Field("path")
    private String path;

    @JsonProperty("method")
    @Field("method")
    private String method;

    @JsonProperty("delete_headers")
    @Field("delete_headers")
    private List<String> deleteHeaders;

    @JsonProperty("add_headers")
    @Field("add_headers")
    private Map<String, Object> addHeaders;
}
