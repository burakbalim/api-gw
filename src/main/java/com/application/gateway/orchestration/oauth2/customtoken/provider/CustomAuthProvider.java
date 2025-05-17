package com.application.gateway.orchestration.oauth2.customtoken.provider;

import com.application.gateway.orchestration.oauth2.model.CustomAuthenticationToken;
import com.application.gateway.orchestration.oauth2.model.User;
import org.springframework.security.oauth2.core.AuthorizationGrantType;

public interface CustomAuthProvider {

    AuthorizationGrantType getGrantType();

    User authenticate(CustomAuthenticationToken authentication);

    default User authenticateAsAdmin(CustomAuthenticationToken customAuthentication) {
        throw new UnsupportedOperationException();
    };
}
