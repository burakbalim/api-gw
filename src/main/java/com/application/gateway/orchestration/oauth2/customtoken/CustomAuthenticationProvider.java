package com.application.gateway.orchestration.oauth2.customtoken;

import com.application.gateway.orchestration.oauth2.customtoken.provider.CustomAuthProvider;
import com.application.gateway.orchestration.oauth2.model.CustomAuthenticationToken;
import com.application.gateway.orchestration.oauth2.model.User;
import com.application.gateway.orchestration.oauth2.model.RegisteredClientDTO;
import com.application.gateway.orchestration.oauth2.service.GwAuthorizationService;
import com.application.gateway.orchestration.oauth2.service.OauthUtils;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2ClientAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationProvider implements AuthenticationProvider {

    private static final String ROLE_ADMIN = "ROLE_ADMIN";

    private final GwAuthorizationService gwAuthorizationService;

    private final ApplicationContext applicationContext;

    private Map<AuthorizationGrantType, CustomAuthProvider> customAuthProviderMap;

    @PostConstruct
    public void init() {
        customAuthProviderMap = applicationContext.getBeanProvider(CustomAuthProvider.class).stream().
                collect(Collectors.toMap(CustomAuthProvider::getGrantType, externalService -> externalService));
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        CustomAuthenticationToken customAuthentication = (CustomAuthenticationToken) authentication;

        OAuth2ClientAuthenticationToken clientPrincipal = OauthUtils.getAuthenticatedClientElseThrowInvalidClient(customAuthentication);

        RegisteredClient registeredClient = clientPrincipal.getRegisteredClient();

        User user = authenticate(customAuthentication, clientPrincipal);

        addSecurityContext(user);

        assert registeredClient != null;

        return gwAuthorizationService.authenticateToRefreshToken(RegisteredClientDTO.from(registeredClient), user, "/oauth2/token");
    }

    private User authenticate(CustomAuthenticationToken customAuthentication, OAuth2ClientAuthenticationToken clientPrincipal) {
        CustomAuthProvider customAuthProvider = customAuthProviderMap.get(customAuthentication.getGrantType());
        User user;
        Set<String> scopes = Objects.requireNonNull(clientPrincipal.getRegisteredClient()).getScopes();
        if (Objects.nonNull(scopes) && scopes.contains(ROLE_ADMIN)) {
            user = customAuthProvider.authenticateAsAdmin(customAuthentication);
        } else {
            user = customAuthProvider.authenticate(customAuthentication);
        }
        return user;
    }

    private void addSecurityContext(User user) {
        OAuth2ClientAuthenticationToken oAuth2ClientAuthenticationToken = (OAuth2ClientAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        oAuth2ClientAuthenticationToken.setDetails(user);
        var securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(oAuth2ClientAuthenticationToken);
        SecurityContextHolder.setContext(securityContext);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return CustomAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
