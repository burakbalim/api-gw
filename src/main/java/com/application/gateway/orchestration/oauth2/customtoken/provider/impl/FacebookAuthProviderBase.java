package com.application.gateway.orchestration.oauth2.customtoken.provider.impl;

import com.application.gateway.common.exception.UnauthorizedException;
import com.application.gateway.main.common.util.LocalDateUtils;
import com.application.gateway.orchestration.oauth2.customtoken.CustomAuthorizationGrantType;
import com.application.gateway.orchestration.oauth2.customtoken.UserResourceService;
import com.application.gateway.orchestration.oauth2.customtoken.provider.CustomAuthProviderBase;
import com.application.gateway.orchestration.oauth2.model.CustomAuthenticationToken;
import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.Version;
import com.restfb.types.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class FacebookAuthProviderBase extends CustomAuthProviderBase {

    @Value("${facebook.app-secret}")
    private String appSecret;

    public FacebookAuthProviderBase(UserResourceService userResourceService) {
        super(userResourceService);
    }

    @Override
    public AuthorizationGrantType getGrantType() {
        return CustomAuthorizationGrantType.FACEBOOK;
    }

    @Override
    protected com.application.gateway.orchestration.oauth2.model.User verify(CustomAuthenticationToken authentication) {
        try {
            FacebookClient facebookClient = new DefaultFacebookClient((String) authentication.getParameters().get(OAuth2ParameterNames.ACCESS_TOKEN),
                    appSecret, Version.LATEST);
            User me = facebookClient.fetchObject("me", User.class);
            return getUser(me);
        } catch (Exception e) {
            throw new UnauthorizedException("Facebook login failed", e);
        }
    }

    private com.application.gateway.orchestration.oauth2.model.User getUser(User user) {
        com.application.gateway.orchestration.oauth2.model.User ouath2User = new com.application.gateway.orchestration.oauth2.model.User();
        ouath2User.setEmail(user.getEmail());
        ouath2User.setAuthProvider(getGrantType().getValue());
        ouath2User.setUsername(user.getEmail().split("@")[0]);
        ouath2User.setExternalId(user.getId());
        ouath2User.setPassword(null);
        if (Objects.nonNull(user.getBirthdayAsDate())) {
            ouath2User.setBirthDate(user.getBirthdayAsDate().toString());
        }
        return ouath2User;
    }


}
