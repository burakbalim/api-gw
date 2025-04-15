package com.application.gateway.main.middleware.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
public class ExtendedPath implements Serializable {

    @JsonProperty("transform_headers")
    @Field("transform_headers")
    private List<TransformHeader> transformHeader;

    @JsonProperty("url_rewrites")
    @Field("url_rewrites")
    private List<UrlRewrites> urlRewrites;

    @JsonProperty("media_type")
    @Field("media_type")
    private String mediaType;
}
