package com.application.gateway.integration.orchestration.oauth2.service;

import com.application.gateway.BaseTest;
import com.application.gateway.common.exception.UnauthorizedException;
import com.application.gateway.orchestration.oauth2.model.RegisteredClientDTO;
import com.application.gateway.orchestration.oauth2.model.User;
import com.application.gateway.orchestration.oauth2.service.GwAuthorizationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AccessTokenAuthenticationToken;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GwAuthorizationServiceTest extends BaseTest {

    @Autowired
    private GwAuthorizationService gwAuthorizationService;

    @Test
    void test_authenticateToClientCredentials_success() {

        RegisteredClientDTO registeredClientDTO = new RegisteredClientDTO();
        registeredClientDTO.setClientId("a3123864-XXXX-XXX-a68c-ab7c910f212e");
        registeredClientDTO.setClientSecret("MDIzMGYyNWMtNjkXXX4Yy00ODAxLTkxZGUtYTliOTQ3OTI4ZWJi");

        OAuth2AccessTokenAuthenticationToken authenticationToken = gwAuthorizationService.authenticateToClientCredentials(registeredClientDTO, "test");

        assertTrue(Objects.nonNull(authenticationToken.getCredentials().toString()));
        assertTrue(Objects.nonNull(authenticationToken.getAccessToken().getTokenValue()));
    }

    @Test
    void test_authenticateToClientCredentials_throw_error() {

        RegisteredClientDTO registeredClientDTO = new RegisteredClientDTO();
        registeredClientDTO.setClientId("43fa1e77-de98-XXXX-XXX-f1c320f65e55");
        registeredClientDTO.setClientSecret("gJAT9AtJ8P4dUz2L8XXXXj0seY5psRpZWLonIaxSCmYjccLmwLYH");

        assertThrows(UnauthorizedException.class, () -> gwAuthorizationService.authenticateToClientCredentials(registeredClientDTO, "test"));
    }

    @Test
    void test_authenticateToRefreshToken_success() {

        RegisteredClientDTO registeredClientDTO = new RegisteredClientDTO();
        registeredClientDTO.setClientId("43fa1e77-de98-XXXX-XXX-f1c320f65e55");
        registeredClientDTO.setClientSecret("gJAT9AtJ8P4dUz2L8XXXXj0seY5psRpZWLonIaxSCmYjccLmwLYH");

        OAuth2AccessTokenAuthenticationToken authenticationToken = gwAuthorizationService.authenticateToRefreshToken(registeredClientDTO, new User(), "test");

        assertTrue(Objects.nonNull(authenticationToken.getCredentials().toString()));
        assertTrue(Objects.nonNull(authenticationToken.getAccessToken().getTokenValue()));
        assertTrue(Objects.nonNull(Objects.requireNonNull(authenticationToken.getRefreshToken()).getTokenValue()));
    }

    @Test
    void test_authenticateToRefreshToken_throw_error() {

        RegisteredClientDTO registeredClientDTO = new RegisteredClientDTO();
        registeredClientDTO.setClientId("a3123864-XXXX-XXX-a68c-ab7c910f212e");
        registeredClientDTO.setClientSecret("gJAT9AtJ8P4dUz2L8XXXXj0seY5psRpZWLonIaxSCmYjccLmwLYH");

        assertThrows(UnauthorizedException.class, () -> gwAuthorizationService.authenticateToClientCredentials(registeredClientDTO, "test"));
    }
}
