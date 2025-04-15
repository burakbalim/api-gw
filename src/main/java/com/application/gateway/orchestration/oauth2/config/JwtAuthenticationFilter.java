package com.application.gateway.orchestration.oauth2.config;

import com.application.gateway.common.exception.UnauthorizedException;
import com.application.gateway.common.properties.AuthProperties;
import com.application.gateway.common.util.PathUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2ClientAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.context.RequestAttributeSecurityContextRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import com.application.gateway.orchestration.common.util.OAuth2ErrorCodesResponseConverter;

import java.io.IOException;
import java.util.Objects;

import static com.application.gateway.common.util.Constants.*;

/**
 * Filter for authenticating requests using JSON Web Tokens (JWT) for OAuth2 authentication.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String ERROR_URI = "https://datatracker.ietf.org/doc/html/rfc6749#section-3.2.1";

    private final JwtDecoder jwtDecoder;

    private final RegisteredClientRepository registeredClientRepository;

    private final AuthProperties authProperties;

    private final RequestAttributeSecurityContextRepository requestAttributeSecurityContextRepository = new RequestAttributeSecurityContextRepository();

    /**
     * Performs JWT authentication for incoming requests.
     *
     * @param request The HTTP servlet request.
     * @param response The HTTP servlet response.
     * @param filterChain The filter chain.
     * @throws ServletException if an error occurs during the filter process.
     * @throws IOException if an I/O error occurs during the filter process.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            checkOauth(request, response);
            filterChain.doFilter(request, response);
        }
        catch (AuthenticationException | UnauthorizedException exception) {
            OAuth2ErrorCodesResponseConverter.convert(response, exception);
        }
    }

    private void checkOauth(HttpServletRequest request, HttpServletResponse response) {
        if (Objects.nonNull(request.getHeader(AUTHORIZATION)) && !request.getHeader(AUTHORIZATION).startsWith(BASIC)) {
            checkOauth2Path(request, response);
        } else if (isBasicAuthPath(request)) {
            checkBasicAuthPath(request);
        }
    }

    private void checkOauth2Path(HttpServletRequest request, HttpServletResponse response) {
        String jwtToken = request.getHeader(AUTHORIZATION).substring(BEARER.length());
        Jwt jwtAssertion = getJwt(jwtToken);
        assert jwtAssertion != null;
        RegisteredClient registeredClient = getRegisteredClient(jwtAssertion);
        assert registeredClient != null;
        ClientAuthenticationMethod clientAuthenticationMethod = registeredClient.getClientSettings().getTokenEndpointAuthenticationSigningAlgorithm() instanceof SignatureAlgorithm ?
                        ClientAuthenticationMethod.PRIVATE_KEY_JWT :
                        ClientAuthenticationMethod.CLIENT_SECRET_JWT;
        OAuth2ClientAuthenticationToken authenticationToken = new OAuth2ClientAuthenticationToken(registeredClient, clientAuthenticationMethod, registeredClient.getClientSecret());
        authenticationToken.setDetails(new WebAuthenticationDetails(request.getRemoteAddr(), null));
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(authenticationToken);
        SecurityContextHolder.setContext(securityContext);
        requestAttributeSecurityContextRepository.saveContext(securityContext, request, response);
    }

    private void checkBasicAuthPath(HttpServletRequest request) {
        String authValue = request.getHeader(GW_AUTHORIZATION);
        if (Objects.isNull(authValue) || !authValue.equals(authProperties.getGwAuthorization())) {
            throw new OAuth2AuthenticationException("Invalid token");
        }
    }

    private RegisteredClient getRegisteredClient(Jwt jwtAssertion) {
        RegisteredClient registeredClient = this.registeredClientRepository.findByClientId((String) jwtAssertion.getClaims().get("sub"));
        if (registeredClient == null) {
            throwInvalidClient(OAuth2ParameterNames.CLIENT_ID, null);
        }
        return registeredClient;
    }

    private Jwt getJwt(String jwtToken) {
        Jwt jwtAssertion = null;
        try {
            jwtAssertion = jwtDecoder.decode(jwtToken);
        }
        catch (JwtException ex) {
            throwInvalidClient(OAuth2ParameterNames.CLIENT_ASSERTION, ex);
        }
        return jwtAssertion;
    }

    private static void throwInvalidClient(String parameterName, Throwable cause) {
        OAuth2Error error = new OAuth2Error(
                OAuth2ErrorCodes.INVALID_CLIENT,
                "Client authentication failed: " + parameterName,
                ERROR_URI
        );
        throw new OAuth2AuthenticationException(error, error.toString(), cause);
    }

    private boolean isBasicAuthPath(HttpServletRequest request) {
        return PathUtils.isPathMatch(BASIC_AUTH_PATHS, request.getRequestURI());
    }
}
