package com.application.gateway.main.common;

import com.application.gateway.orchestration.oauth2.model.ClientType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
public class SessionDTO {

    private String clientId;

    private ClientType clientType;

    private Map<String, String> metaData;

    public SessionDTO(String clientId, ClientType clientType) {
        this.metaData = new HashMap<>();
        this.clientId = clientId;
        this.clientType = clientType;
    }

    public SessionDTO(ClientType clientType) {
        this.metaData = new HashMap<>();
        this.clientType = clientType;
    }
}
