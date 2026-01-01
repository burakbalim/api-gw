package com.application.gateway.orchestration.oauth2.customtoken.provider.impl;

import com.application.gateway.common.exception.UnauthorizedException;
import com.application.gateway.orchestration.oauth2.customtoken.CustomAuthorizationGrantType;
import com.application.gateway.orchestration.oauth2.customtoken.UserResourceService;
import com.application.gateway.orchestration.oauth2.customtoken.provider.CustomAuthProviderBase;
import com.application.gateway.orchestration.oauth2.model.CustomAuthenticationToken;
import com.application.gateway.orchestration.oauth2.model.User;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;

@Service
@ConditionalOnProperty(value="sign.google.enabled", havingValue = "true")
public class GoogleAuthProviderService extends CustomAuthProviderBase {

    @Value("${google.clientId}")
    private String clientId;

    @Value("${google.adminClientId}")
    private String clientAdminId;

    public GoogleAuthProviderService(UserResourceService userResourceService) {
        super(userResourceService);
    }

    @Override
    public AuthorizationGrantType getGrantType() {
        return CustomAuthorizationGrantType.GOOGLE;
    }

    @Override
    protected User verifyAsAdmin(CustomAuthenticationToken authentication) {
        GoogleIdToken.Payload googlePayload = verifyGoogleToken((String) authentication.getParameters().get(OAuth2ParameterNames.ACCESS_TOKEN), clientAdminId);
        return getUser(googlePayload);
    }

    @Override
    protected User verify(CustomAuthenticationToken authentication) {
        GoogleIdToken.Payload googlePayload = verifyGoogleToken((String) authentication.getParameters().get(OAuth2ParameterNames.ACCESS_TOKEN), clientId);
        return getUser(googlePayload);
    }

    private User getUser(GoogleIdToken.Payload googlePayload) {
        User user = new User();
        user.setEmail(googlePayload.getEmail());
        user.setAuthProvider(getGrantType().getValue());
        user.setUsername(googlePayload.getEmail().split("@")[0]);
        user.setExternalId(googlePayload.getJwtId());
        user.setPassword(null);
        user.setBirthDate(null);
        return user;
    }

    public GoogleIdToken.Payload verifyGoogleToken(String idTokenString, String clientId) {
        try {
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                    new NetHttpTransport(), new GsonFactory())
                    .setAudience(Collections.singletonList(clientId))
                    .build();

            GoogleIdToken idToken = verifier.verify(idTokenString);

            if (idToken != null) {
                return idToken.getPayload();
            } else {
                throw new UnauthorizedException("Invalid ID token.");
            }
        } catch (GeneralSecurityException | IOException e) {
            throw new UnauthorizedException("Error verifying Google token: " + e.getMessage(), e);
        }
    }
}
