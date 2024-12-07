package com.application.gateway.orchestration.oauth2.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Getter
@Setter
public class PathConfiguration {

    @JsonProperty("type")
    @Field(name = "type")
    private ClientType type;

    @JsonProperty("paths")
    @Field("paths")
    private List<String> paths;
}
