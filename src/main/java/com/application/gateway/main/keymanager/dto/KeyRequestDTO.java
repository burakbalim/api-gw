package com.application.gateway.main.keymanager.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Map;

@Getter
@Setter
public class KeyRequestDTO implements Serializable {

    @NotNull
    @JsonProperty("expires")
    private Long expireSecs;

    @JsonProperty("org_id")
    private String orgId;

    @NotNull
    @JsonProperty("access_rights")
    private AccessRightDTO accessRightDTO;

    @JsonProperty("meta_data")
    private Map<String, String> metaData;

    @NotNull
    @JsonProperty("basic_auth_data")
    private BasicAuthDataDTO basicAuthDataDTO;

}
