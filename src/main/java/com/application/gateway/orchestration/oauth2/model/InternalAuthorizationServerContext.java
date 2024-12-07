package com.application.gateway.orchestration.oauth2.model;

import org.springframework.security.oauth2.server.authorization.context.AuthorizationServerContext;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;

public class InternalAuthorizationServerContext implements AuthorizationServerContext {

    private final AuthorizationServerSettings authorizationServerSettings;

    private final String issuer;

    public InternalAuthorizationServerContext(String issuer, AuthorizationServerSettings authorizationServerSettings) {
        this.issuer = issuer;
        this.authorizationServerSettings = authorizationServerSettings;
    }

    @Override
    public String getIssuer() {
        return this.issuer;
    }

    @Override
    public AuthorizationServerSettings getAuthorizationServerSettings() {
        return this.authorizationServerSettings;
    }
}
