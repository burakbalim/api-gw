package com.application.gateway.orchestration.oauth2.service;

import com.application.gateway.common.exception.ClientUnauthorizedResponse;
import com.application.gateway.common.exception.UnauthorizedException;
import lombok.Getter;

@Getter
public class ClientUnauthorizedException extends UnauthorizedException {

    private final ClientUnauthorizedResponse clientUnauthorizedResponse;

    private static final String ERROR = "invalid_request";

    public ClientUnauthorizedException(String errorDescription) {
        super(errorDescription);
        this.clientUnauthorizedResponse = new ClientUnauthorizedResponse(ERROR, errorDescription);
    }
}
