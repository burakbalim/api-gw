package com.application.gateway.orchestration.oauth2.customtoken.provider.impl;

import com.application.gateway.common.exception.UnauthorizedException;
import com.application.gateway.orchestration.oauth2.customtoken.CustomAuthorizationGrantType;
import com.application.gateway.orchestration.oauth2.customtoken.UserResourceService;
import com.application.gateway.orchestration.oauth2.customtoken.provider.CustomAuthProviderBase;
import com.application.gateway.orchestration.oauth2.model.CustomAuthenticationToken;
import com.restfb.types.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;

@Service
@ConditionalOnProperty(value="sign.facebook.enabled", havingValue = "true")
public class FacebookAuthProviderBase extends CustomAuthProviderBase {

    @Value("${facebook.app-secret}")
    private String appSecret;

    //@Value("${facebook.app-id}")
    private String appId;

    private final RestTemplate restTemplate;

    public FacebookAuthProviderBase(UserResourceService userResourceService, RestTemplate restTemplate) {
        super(userResourceService);
        this.restTemplate = restTemplate;
    }

    @Override
    public AuthorizationGrantType getGrantType() {
        return CustomAuthorizationGrantType.FACEBOOK;
    }

    @Override
    protected com.application.gateway.orchestration.oauth2.model.User verifyAsAdmin(CustomAuthenticationToken authentication) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected com.application.gateway.orchestration.oauth2.model.User verify(CustomAuthenticationToken authentication) {
        String accessToken = (String) authentication.getParameters().get(OAuth2ParameterNames.ACCESS_TOKEN);
        String debugUrl = String.format(
                "https://graph.facebook.com/debug_token?input_token=%s&access_token=%s|%s",
                accessToken, appId, appSecret
        );
        try {
            ResponseEntity<Object> response = restTemplate.exchange(
                    debugUrl, HttpMethod.GET, null, Object.class
            );
            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new UnauthorizedException("Invalid Facebook access token");
            }
            String meUrl = String.format(
                    "https://graph.facebook.com/me?fields=id,email,birthday&access_token=%s",
                    accessToken
            );
            ResponseEntity<User> meResponse = restTemplate.exchange(
                    meUrl, HttpMethod.GET, null, User.class
            );
            return getUser(meResponse.getBody());

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

