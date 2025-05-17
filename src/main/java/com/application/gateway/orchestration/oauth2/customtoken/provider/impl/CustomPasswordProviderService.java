package com.application.gateway.orchestration.oauth2.customtoken.provider.impl;

import com.application.gateway.common.exception.UnauthorizedException;
import com.application.gateway.orchestration.oauth2.customtoken.CustomAuthorizationGrantType;
import com.application.gateway.orchestration.oauth2.customtoken.UserResourceService;
import com.application.gateway.orchestration.oauth2.customtoken.provider.CustomAuthProviderBase;
import com.application.gateway.orchestration.oauth2.model.CustomAuthenticationToken;
import com.application.gateway.orchestration.oauth2.model.User;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class CustomPasswordProviderService extends CustomAuthProviderBase {

    private final UserResourceService userResourceService;

    public CustomPasswordProviderService(UserResourceService userResourceService) {
        super(userResourceService);
        this.userResourceService = userResourceService;
    }

    @Override
    public AuthorizationGrantType getGrantType() {
        return CustomAuthorizationGrantType.CUSTOM_PASSWORD;
    }

    @Override
    protected User verify(CustomAuthenticationToken authentication) {
        Map<String, Object> parameters = authentication.getParameters();
        String username = (String) parameters.get("username");
        Boolean isValidUser = this.userResourceService.validatePassword(username, (String) parameters.get("password"));
        if (!isValidUser) {
            throw new UnauthorizedException("User is not valid");
        }
        return new User(username, "Local");
    }

    @Override
    protected User verifyAsAdmin(CustomAuthenticationToken authentication) {
        Map<String, Object> parameters = authentication.getParameters();
        String username = (String) parameters.get("username");
        Boolean isValidUser = this.userResourceService.validateAdminPassword(username, (String) parameters.get("password"));
        if (!isValidUser) {
            throw new UnauthorizedException("User is not valid");
        }
        return new User(username, "Local");
    }
}
