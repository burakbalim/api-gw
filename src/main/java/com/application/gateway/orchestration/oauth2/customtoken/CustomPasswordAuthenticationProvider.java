package com.application.gateway.orchestration.oauth2.customtoken;

import com.application.gateway.common.util.Constants;
import com.application.gateway.orchestration.oauth2.model.CustomPasswordAuthenticationToken;
import com.application.gateway.orchestration.oauth2.model.UserDTO;
import com.application.gateway.orchestration.oauth2.service.OauthUtils;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.*;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AccessTokenAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2ClientAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.context.AuthorizationServerContext;
import org.springframework.security.oauth2.server.authorization.context.AuthorizationServerContextHolder;
import org.springframework.security.oauth2.server.authorization.token.DefaultOAuth2TokenContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenGenerator;

import java.security.Principal;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class CustomPasswordAuthenticationProvider implements AuthenticationProvider {

    private static final String ERROR_URI = "https://datatracker.ietf.org/doc/html/rfc6749#section-5.2";

    private final OAuth2AuthorizationService authorizationService;

    private final UserService userService;

    private final OAuth2TokenGenerator<? extends OAuth2Token> tokenGenerator;

    public CustomPasswordAuthenticationProvider(OAuth2AuthorizationService authorizationService,
                                                OAuth2TokenGenerator<? extends OAuth2Token> tokenGenerator,
                                                UserService userService) {
        this.authorizationService = authorizationService;
        this.tokenGenerator = tokenGenerator;
        this.userService = userService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        CustomPasswordAuthenticationToken authenticationToken = (CustomPasswordAuthenticationToken) authentication;

        OAuth2ClientAuthenticationToken clientPrincipal = OauthUtils.getAuthenticatedClientElseThrowInvalidClient(authenticationToken);

        RegisteredClient registeredClient = clientPrincipal.getRegisteredClient();

        UserDTO user = userService.getUserDTO(authenticationToken.getUsername(), authenticationToken.getPassword());

        addSecurityContext(user);

        AuthorizationServerContext context = AuthorizationServerContextHolder.getContext();

        DefaultOAuth2TokenContext.Builder authTokenContext = DefaultOAuth2TokenContext.builder().registeredClient(registeredClient)
                .principal(clientPrincipal)
                .authorizationServerContext(context)
                .authorizedScopes(new HashSet<>())
                .authorizationGrantType(new AuthorizationGrantType(Constants.CUSTOM_PASSWORD_GRANT_TYPE))
                .authorizationGrant(authenticationToken);

        DefaultOAuth2TokenContext accessTokenContext = authTokenContext.tokenType(OAuth2TokenType.ACCESS_TOKEN).build();
        DefaultOAuth2TokenContext refreshTokenContext = authTokenContext.tokenType(OAuth2TokenType.REFRESH_TOKEN).build();

        OAuth2Token oAuth2AccessToken = tokenGenerator.generate(accessTokenContext);
        OAuth2Token oAuth2RefreshToken = tokenGenerator.generate(refreshTokenContext);

        assert registeredClient != null;
        OAuth2Authorization.Builder authorizationBuilder = OAuth2Authorization.withRegisteredClient(registeredClient).attribute(Principal.class.getName(), clientPrincipal)
                .principalName(clientPrincipal.getName())
                .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                //.authorizationGrantType(new AuthorizationGrantType("custom_password"))
                .authorizedScopes(Collections.emptySet());

        assert oAuth2AccessToken != null;
        OAuth2AccessToken accessToken = new OAuth2AccessToken(OAuth2AccessToken.TokenType.BEARER, oAuth2AccessToken.getTokenValue(), oAuth2AccessToken.getIssuedAt(),
                oAuth2AccessToken.getExpiresAt(), accessTokenContext.getAuthorizedScopes());

        assert oAuth2RefreshToken != null;
        OAuth2RefreshToken refreshToken = new OAuth2RefreshToken(oAuth2RefreshToken.getTokenValue(), oAuth2RefreshToken.getIssuedAt(), oAuth2RefreshToken.getExpiresAt());

        authorizationBuilder.refreshToken(refreshToken);
        authorizationBuilder.accessToken(accessToken);

        this.authorizationService.save(authorizationBuilder.build());

        return new OAuth2AccessTokenAuthenticationToken(registeredClient, clientPrincipal, accessToken, refreshToken);
    }

    private void addSecurityContext(UserDTO user) {
        OAuth2ClientAuthenticationToken oAuth2ClientAuthenticationToken = (OAuth2ClientAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        oAuth2ClientAuthenticationToken.setDetails(user);
        var newcontext = SecurityContextHolder.createEmptyContext();
        newcontext.setAuthentication(oAuth2ClientAuthenticationToken);
        SecurityContextHolder.setContext(newcontext);
    }


    private static DefaultOAuth2TokenContext.Builder prepareOAuth2TokenContext(Set<String> authorizedScopes, CustomPasswordAuthenticationToken customPasswordAuthenticationToken, OAuth2ClientAuthenticationToken clientPrincipal, RegisteredClient registeredClient) {
        return DefaultOAuth2TokenContext.builder().registeredClient(registeredClient)
                .principal(clientPrincipal)
                .authorizationServerContext(AuthorizationServerContextHolder.getContext())
                .authorizedScopes(authorizedScopes)
                .authorizationGrantType(new AuthorizationGrantType(Constants.CUSTOM_PASSWORD_GRANT_TYPE))
                .authorizationGrant(customPasswordAuthenticationToken);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return CustomPasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }


    private OAuth2RefreshToken getRefreshToken(OAuth2ClientAuthenticationToken clientPrincipal,
                                               RegisteredClient registeredClient, OAuth2Authorization.Builder authorizationBuilder, DefaultOAuth2TokenContext.Builder tokenContextBuilder) {
        OAuth2RefreshToken refreshToken = null;
        if (registeredClient.getAuthorizationGrantTypes().contains(AuthorizationGrantType.REFRESH_TOKEN) &&
                !clientPrincipal.getClientAuthenticationMethod().equals(ClientAuthenticationMethod.NONE)) {
            OAuth2Token generatedRefreshToken = generateToken(tokenContextBuilder.tokenType(OAuth2TokenType.REFRESH_TOKEN).build());
            refreshToken = (OAuth2RefreshToken) generatedRefreshToken;
            authorizationBuilder.refreshToken(refreshToken);
        }
        return refreshToken;
    }

    private OAuth2Token generateToken(OAuth2TokenContext tokenContext) {
        OAuth2Token generatedAccessToken = this.tokenGenerator.generate(tokenContext);
        if (generatedAccessToken == null) {
            OAuth2Error error = new OAuth2Error(OAuth2ErrorCodes.SERVER_ERROR, "The token generator failed to generate the access token.", ERROR_URI);
            throw new OAuth2AuthenticationException(error);
        }
        return generatedAccessToken;
    }
}
