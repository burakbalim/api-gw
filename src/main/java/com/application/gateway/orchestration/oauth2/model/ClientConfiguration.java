package com.application.gateway.orchestration.oauth2.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;

@Getter
@Setter
public class ClientConfiguration implements Serializable {

    @JsonProperty("name")
    @Field("name")
    private String name;

    @JsonProperty("grant_type")
    @Field("grant_type")
    private String grantType;

    @JsonProperty("type")
    @Field("type")
    private ClientType clientType;

    @JsonProperty("client_id")
    @Field("client_id")
    private String clientId;

    @JsonProperty("client_secret")
    @Field("client_secret")
    private String clientSecret;

    @JsonProperty("access_token_exp")
    @Field("access_token_exp")
    private Integer accessTokenExp;

    @JsonProperty("refresh_token_exp")
    @Field("refresh_token_exp")
    private Integer refreshTokenExp;
}
