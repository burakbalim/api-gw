package com.application.gateway.orchestration.oauth2.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RegisteredClientDTO {

    private String clientId;

    private String clientSecret;
}
