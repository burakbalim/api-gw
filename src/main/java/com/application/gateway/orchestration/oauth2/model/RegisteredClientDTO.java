package com.application.gateway.orchestration.oauth2.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RegisteredClientDTO {

    private String clientId;

    private String clientSecret;

    public static RegisteredClientDTO from(RegisteredClient registeredClient) {
        RegisteredClientDTO registeredClientDTO = new RegisteredClientDTO();
        registeredClientDTO.clientId = registeredClient.getClientId();
        registeredClientDTO.clientSecret = registeredClient.getClientSecret();
        return registeredClientDTO;
    }
}
