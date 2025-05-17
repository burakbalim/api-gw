package com.application.gateway.orchestration.oauth2.customtoken.provider;

import com.application.gateway.orchestration.oauth2.customtoken.UserResourceService;
import com.application.gateway.orchestration.oauth2.model.CustomAuthenticationToken;
import com.application.gateway.orchestration.oauth2.model.User;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public abstract class CustomAuthProviderBase implements CustomAuthProvider {

    private final UserResourceService userResourceService;

    protected abstract User verifyAsAdmin(CustomAuthenticationToken authentication);
    protected abstract User verify(CustomAuthenticationToken authentication);

    @Override
    public User authenticateAsAdmin(CustomAuthenticationToken customAuthentication) {
        User user = verifyAsAdmin(customAuthentication);

        return userResourceService.createAdminOrGet(user);
    }

    public User authenticate(CustomAuthenticationToken authentication) {
        User user = verify(authentication);

        return userResourceService.createOrGet(user);
    }

    public String usernameParser(String email) {
        return (email).split("@")[0];
    }
}
