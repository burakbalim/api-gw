package com.application.gateway.orchestration.oauth2.service;

import com.application.gateway.orchestration.oauth2.exception.ClientUnauthorizedException;
import com.application.gateway.orchestration.oauth2.model.InternalAuthorizationServerContext;
import com.application.gateway.orchestration.oauth2.model.RegisteredClientDTO;
import com.application.gateway.orchestration.oauth2.model.User;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.*;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AccessTokenAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2ClientAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.context.AuthorizationServerContextHolder;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.token.DefaultOAuth2TokenContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenGenerator;

import java.security.Principal;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;

public class GwAuthorizationService {

    private final RegisteredClientRepository registeredClientRepository;

    private final OAuth2TokenGenerator<? extends OAuth2Token> tokenGenerator;

    private final OAuth2AuthorizationService authorizationService;

    private final AuthorizationServerSettings authorizationServerSettings;

    public GwAuthorizationService(RegisteredClientRepository registeredClientRepository, OAuth2TokenGenerator<? extends OAuth2Token> tokenGenerator, OAuth2AuthorizationService authorizationService, AuthorizationServerSettings authorizationServerSettings) {
        this.registeredClientRepository = registeredClientRepository;
        this.tokenGenerator = tokenGenerator;
        this.authorizationService = authorizationService;
        this.authorizationServerSettings = authorizationServerSettings;
    }

    public OAuth2AccessTokenAuthenticationToken authenticateToRefreshToken(RegisteredClientDTO registeredClientDTO, User user, String uri) {
        RegisteredClient registeredClient = registeredClientRepository.findByClientId(registeredClientDTO.getClientId());

        validateRegisteredClient(registeredClientDTO, registeredClient, AuthorizationGrantType.REFRESH_TOKEN);

        addSecurityContext(registeredClient, user);

        AuthorizationServerContextHolder.setContext(new InternalAuthorizationServerContext(uri, authorizationServerSettings));

        OAuth2ClientAuthenticationToken clientPrincipal = OauthUtils.getAuthenticatedClientElseThrowInvalidClient(SecurityContextHolder.getContext().getAuthentication());

        assert registeredClient != null;
        DefaultOAuth2TokenContext.Builder authTokenContext = DefaultOAuth2TokenContext.builder().registeredClient(registeredClient)
                .principal(clientPrincipal)
                .authorizationServerContext(AuthorizationServerContextHolder.getContext())
                .authorizedScopes(registeredClient.getScopes())
                .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN);

        OAuth2AccessToken accessToken = (OAuth2AccessToken) generateToken(authTokenContext, OAuth2TokenType.ACCESS_TOKEN);
        OAuth2RefreshToken refreshToken = (OAuth2RefreshToken) generateToken(authTokenContext, OAuth2TokenType.REFRESH_TOKEN);

        OAuth2Authorization.Builder authorizationBuilder = OAuth2Authorization.withRegisteredClient(registeredClient)
                .attribute(Principal.class.getName(), clientPrincipal)
                .principalName(clientPrincipal.getName())
                .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                .authorizedScopes(registeredClient.getScopes())
                .refreshToken(refreshToken)
                .accessToken(accessToken);

        OAuth2Authorization oAuth2Authorization = authorizationBuilder.build();

        this.authorizationService.save(oAuth2Authorization);

        return new OAuth2AccessTokenAuthenticationToken(registeredClient, clientPrincipal, accessToken, refreshToken);
    }

    public OAuth2AccessTokenAuthenticationToken authenticateToClientCredentials(RegisteredClientDTO registeredClientDTO, String uri) {
        RegisteredClient registeredClient = registeredClientRepository.findByClientId(registeredClientDTO.getClientId());

        validateRegisteredClient(registeredClientDTO, registeredClient, AuthorizationGrantType.CLIENT_CREDENTIALS);

        addSecurityContext(registeredClient, null);

        AuthorizationServerContextHolder.setContext(new InternalAuthorizationServerContext(uri, authorizationServerSettings));

        OAuth2ClientAuthenticationToken clientPrincipal = OauthUtils.getAuthenticatedClientElseThrowInvalidClient(SecurityContextHolder.getContext().getAuthentication());

        DefaultOAuth2TokenContext.Builder authContext = DefaultOAuth2TokenContext.builder().registeredClient(registeredClient)
                .principal(clientPrincipal)
                .authorizationServerContext(AuthorizationServerContextHolder.getContext())
                .authorizedScopes(new HashSet<>())
                .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS);

        OAuth2AccessToken accessToken = (OAuth2AccessToken) generateToken(authContext, OAuth2TokenType.ACCESS_TOKEN);

        assert registeredClient != null;
        OAuth2Authorization.Builder authorizationBuilder = OAuth2Authorization.withRegisteredClient(registeredClient)
                .attribute(Principal.class.getName(), clientPrincipal)
                .principalName(clientPrincipal.getName())
                .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
                .authorizedScopes(Collections.emptySet())
                .accessToken(accessToken);

        OAuth2Authorization oAuth2Authorization = authorizationBuilder.build();

        this.authorizationService.save(oAuth2Authorization);

        return new OAuth2AccessTokenAuthenticationToken(registeredClient, clientPrincipal, accessToken);
    }

    private void addSecurityContext(RegisteredClient registeredClient, User user) {
        OAuth2ClientAuthenticationToken oAuth2ClientAuthenticationToken = new OAuth2ClientAuthenticationToken(registeredClient, ClientAuthenticationMethod.CLIENT_SECRET_JWT, null);
        oAuth2ClientAuthenticationToken.setDetails(user);
        var newContext = SecurityContextHolder.createEmptyContext();
        newContext.setAuthentication(oAuth2ClientAuthenticationToken);
        SecurityContextHolder.setContext(newContext);
    }

    private AbstractOAuth2Token generateToken(DefaultOAuth2TokenContext.Builder defaultOAuth2TokenContext, OAuth2TokenType type) {
        DefaultOAuth2TokenContext buildedDefaultOauth2TokenContext = defaultOAuth2TokenContext.tokenType(type).build();
        OAuth2Token oauth2Token = tokenGenerator.generate(buildedDefaultOauth2TokenContext);

        assert oauth2Token != null;

        return type.equals(OAuth2TokenType.ACCESS_TOKEN) ?
                new OAuth2AccessToken(OAuth2AccessToken.TokenType.BEARER, oauth2Token.getTokenValue(), oauth2Token.getIssuedAt(), oauth2Token.getExpiresAt(),
                        buildedDefaultOauth2TokenContext.getAuthorizedScopes()) :
                new OAuth2RefreshToken(oauth2Token.getTokenValue(), oauth2Token.getIssuedAt(), oauth2Token.getExpiresAt());
    }

    private static void validateRegisteredClient(RegisteredClientDTO registeredClientDTO, RegisteredClient registeredClient, AuthorizationGrantType authorizationGrantType) {
        if (Objects.isNull(registeredClientDTO.getClientSecret()) || Objects.isNull(registeredClient) || Objects.isNull(registeredClient.getClientSecret()))  {
            throw new ClientUnauthorizedException("The client is not authorized to request a token using this method.");
        }
        if (!registeredClientDTO.getClientSecret().equals(registeredClient.getClientSecret()) || !registeredClient.getAuthorizationGrantTypes().contains(authorizationGrantType)) {
            throw new ClientUnauthorizedException("The authorization server encountered an unexpected condition that prevented it from fulfilling the request");
        }
    }
}
