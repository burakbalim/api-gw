package com.application.gateway.orchestration.oauth2.customtoken;

import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.springframework.security.oauth2.core.oidc.IdTokenClaimNames.NONCE;

public class CustomAuthorizationGrantType  {

    public static final AuthorizationGrantType CUSTOM_PASSWORD = new AuthorizationGrantType("custom_password");

    public static final AuthorizationGrantType GOOGLE = new AuthorizationGrantType("google");

    public static final AuthorizationGrantType FACEBOOK = new AuthorizationGrantType("facebook");

    public static final AuthorizationGrantType APPLE = new AuthorizationGrantType("apple");

    public static final Set<AuthorizationGrantType> AUTH_GRANT_TYPE_SET = Set.of(CUSTOM_PASSWORD, GOOGLE, FACEBOOK, APPLE);

    private static final Map<AuthorizationGrantType, Set<String>> AUTHORIZATION_GRANT_TYPE_PARAM_MAP = Map.of(
            GOOGLE, Set.of(OAuth2ParameterNames.ACCESS_TOKEN),
            FACEBOOK, Set.of(OAuth2ParameterNames.ACCESS_TOKEN),
            APPLE, Set.of(OAuth2ParameterNames.ACCESS_TOKEN, NONCE),
            CUSTOM_PASSWORD, Set.of(OAuth2ParameterNames.USERNAME, OAuth2ParameterNames.PASSWORD)
    );

    public static void validate(AuthorizationGrantType authorizationGrantType, MultiValueMap<String, String> parameters) {
        validateScope(parameters);
        Set<String> params = Optional.ofNullable(AUTHORIZATION_GRANT_TYPE_PARAM_MAP.get(authorizationGrantType)).orElseThrow(() -> new OAuth2AuthenticationException(OAuth2ErrorCodes.UNSUPPORTED_GRANT_TYPE));
        params.forEach(param -> validateRequiredParam(parameters, param));
    }

    private static void validateScope(MultiValueMap<String, String> parameters) {
        String scope = parameters.getFirst(OAuth2ParameterNames.SCOPE);
        if (StringUtils.hasText(scope) && parameters.get(OAuth2ParameterNames.SCOPE).size() != 1) {
            throw new OAuth2AuthenticationException(OAuth2ErrorCodes.INVALID_REQUEST);
        }
    }

    private static void validateRequiredParam(MultiValueMap<String, String> parameters, String paramName) {
        String value = parameters.getFirst(paramName);
        if (!StringUtils.hasText(value) || parameters.get(paramName).size() != 1) {
            throw new OAuth2AuthenticationException(OAuth2ErrorCodes.INVALID_REQUEST);
        }
    }
}
