package com.application.gateway.orchestration.oauth2.customtoken.provider.impl;

import com.application.gateway.common.exception.UnauthorizedException;
import com.application.gateway.orchestration.oauth2.customtoken.CustomAuthorizationGrantType;
import com.application.gateway.orchestration.oauth2.customtoken.UserResourceService;
import com.application.gateway.orchestration.oauth2.customtoken.provider.CustomAuthProviderBase;
import com.application.gateway.orchestration.oauth2.model.CustomAuthenticationToken;
import com.application.gateway.orchestration.oauth2.model.User;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.springframework.security.oauth2.core.oidc.IdTokenClaimNames.NONCE;

@Service
public class AppleAuthProviderService extends CustomAuthProviderBase {

    private static final String APPLE_JWKS_URI = "https://appleid.apple.com/auth/keys";
    private static final String APPLE_ISSUER = "https://appleid.apple.com";
    private static final String EMAIL = "email";

    private JwtDecoder jwtDecoder;

    public AppleAuthProviderService(UserResourceService userResourceService) {
        super(userResourceService);
    }

    @PostConstruct
    public void initialize() {
        this.jwtDecoder = verifyAppleIdToken();
    }

    @Override
    public AuthorizationGrantType getGrantType() {
        return CustomAuthorizationGrantType.APPLE;
    }

    @Override
    protected User verifyAsAdmin(CustomAuthenticationToken authentication) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected User verify(CustomAuthenticationToken authentication) {
        try {
            Jwt jwt = jwtDecoder.decode((String) authentication.getParameters().get(OAuth2ParameterNames.ACCESS_TOKEN));
            checkNonceParameter(jwt.getClaim(NONCE), (String) authentication.getParameters().get(NONCE));
            return getUser(jwt);
        } catch (Exception e) {
            throw new UnauthorizedException("Apple token verification failed", e);
        }
    }

    private void checkNonceParameter(String jwtNonce, String paramNonce) throws NoSuchAlgorithmException {
        if (!jwtNonce.equals(sha256(paramNonce))) {
            throw new UnauthorizedException("Apple nonce verification failed");
        }
    }

    private User getUser(Jwt jwt) {
        User user = new User();
        user.setAuthProvider("apple");
        user.setExternalId(jwt.getSubject());
        user.setEmail(jwt.getClaim(EMAIL));
        user.setUsername(usernameParser(jwt.getClaim("email")));
        return user;
    }

    public NimbusJwtDecoder verifyAppleIdToken() {
        NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder.withJwkSetUri(APPLE_JWKS_URI).build();
        List<OAuth2TokenValidator<Jwt>> validators = new ArrayList<>();
        validators.add(new JwtIssuerValidator(APPLE_ISSUER));
        validators.add(new JwtClaimValidator<>(NONCE, Objects::nonNull));
        validators.add(new JwtClaimValidator<>(EMAIL, Objects::nonNull));
        validators.add(new JwtTimestampValidator());
        OAuth2TokenValidator<Jwt> validator = new DelegatingOAuth2TokenValidator<>(validators);
        jwtDecoder.setJwtValidator(validator);
        return jwtDecoder;
    }

    public static String sha256(String originalNonce) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hashBytes = digest.digest(originalNonce.getBytes());
        StringBuilder hexString = new StringBuilder();
        for (byte b : hashBytes) {
            hexString.append(String.format("%02x", b));
        }
        return hexString.toString();
    }
}
