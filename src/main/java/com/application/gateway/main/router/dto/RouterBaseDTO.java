package com.application.gateway.main.router.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.application.gateway.orchestration.oauth2.model.ClientType;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
public class RouterBaseDTO implements Serializable {

    @JsonProperty("service_name")
    @Field("service_name")
    private String serviceName;

    @JsonProperty("service_url")
    @Field("service_url")
    private String serviceUrl;

    @JsonProperty("authorization")
    @Field("authorization")
    private AuthInfoDTO authInfoDTO;

    @JsonProperty("access_client")
    @Field("access_client")
    private List<ClientType> accessClient;

}
