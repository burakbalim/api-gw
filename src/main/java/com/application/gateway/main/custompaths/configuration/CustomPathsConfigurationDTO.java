package com.application.gateway.main.custompaths.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Getter
@Setter
public class CustomPathsConfigurationDTO {

    @JsonProperty("name")
    @Field("name")
    private String name;

    @JsonProperty("api_id")
    @Field("api_id")
    private String apiId;

    @JsonProperty("use_basic_auth")
    @Field("use_basic_auth")
    private boolean useBasicAuth;

    @JsonProperty("use_session")
    @Field("use_session")
    private Boolean useSession;

    @JsonProperty("pre")
    @Field("pre")
    private PathRules pre;

    @JsonProperty("post")
    @Field("post")
    private PathRules post;

    @JsonProperty("paths")
    @Field("paths")
    private List<PathDTO> pathDTOS;

    @JsonProperty("listen_path")
    @Field("listen_path")
    private String listenPath;

    @JsonProperty("policy")
    @Field("policy")
    private String policy;
}
