package com.application.gateway.main.standardendpoint;

import com.application.gateway.common.util.Decoder;
import com.application.gateway.main.common.VirtualEndpointRequestInfo;
import com.application.gateway.orchestration.base.sdk.VirtualEndpointBase;
import com.application.gateway.orchestration.oauth2.model.RegisteredClientDTO;
import com.application.gateway.orchestration.oauth2.service.ClientUnauthorizedException;
import com.application.gateway.orchestration.oauth2.service.GwAuthorizationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AccessTokenAuthenticationToken;
import org.springframework.stereotype.Component;

import java.time.temporal.ChronoUnit;
import java.util.Objects;

@RequiredArgsConstructor
@Component("clientCredentials")
@Slf4j
public class ClientCredentialsEndpoint extends VirtualEndpointBase {

    private final GwAuthorizationService gwAuthorizationService;

    private static final String AUTHORIZATION = "Authorization";

    @Override
    public ResponseEntity<Object> apply(VirtualEndpointRequestInfo requestInfo) {
        String authorization = requestInfo.getHeader(AUTHORIZATION).iterator().next();
        RegisteredClientDTO registeredClient = getRegisteredClient(authorization);
        OAuth2AccessTokenAuthenticationToken authenticationToken = getAuthenticationToken(requestInfo, registeredClient);
        OAuth2AccessToken accessToken = authenticationToken.getAccessToken();
        return new ResponseEntity<>(new AuthTokenResponse(accessToken.getTokenValue(), ChronoUnit.SECONDS.between(Objects.requireNonNull(accessToken.getIssuedAt()), accessToken.getExpiresAt()),
                OAuth2AccessToken.TokenType.BEARER.getValue()), HttpStatus.OK);
    }

    private OAuth2AccessTokenAuthenticationToken getAuthenticationToken(VirtualEndpointRequestInfo requestInfo, RegisteredClientDTO registeredClient) {
        try {
            return gwAuthorizationService.authenticateToClientCredentials(registeredClient, requestInfo.getUri());
        } catch (ClientUnauthorizedException e) {
            throw e;
        } catch (Exception e) {
            log.warn("Unexpected exception while authentication request. Message: {}", e.getMessage());
            throw new ClientUnauthorizedException("The authorization server encountered an unexpected condition that prevented it from fulfilling the request.");
        }
    }

    private RegisteredClientDTO getRegisteredClient(String headerValue) {
        if (headerValue.isEmpty()) {
            throw new ClientUnauthorizedException("The authorization server encountered an unexpected condition that prevented it from fulfilling the request.");
        }
        String[] credentialsValue = Decoder.decodeAuthorizationHeader(headerValue);
        if (credentialsValue.length == 1) {
            throw new ClientUnauthorizedException("The client is not authorized to request a token using this method.");
        }
        return new RegisteredClientDTO(credentialsValue[0], credentialsValue[1]);
    }
}
