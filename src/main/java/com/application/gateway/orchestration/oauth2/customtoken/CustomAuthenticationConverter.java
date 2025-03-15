package com.application.gateway.orchestration.oauth2.customtoken;

import com.application.gateway.orchestration.oauth2.model.CustomAuthenticationToken;
import com.application.gateway.orchestration.oauth2.service.OauthUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.lang.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2ClientAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;

import java.util.*;

public class CustomAuthenticationConverter implements AuthenticationConverter {

	@Nullable
	@Override
	public Authentication convert(HttpServletRequest request) {
		String grantType = request.getParameter(OAuth2ParameterNames.GRANT_TYPE);
		if (grantType == null) {
			return null;
		}

		AuthorizationGrantType authorizationGrantType = new AuthorizationGrantType(grantType);
		if (!CustomAuthorizationGrantType.AUTH_GRANT_TYPE_SET.contains(authorizationGrantType)) {
			return null;
		}

		OAuth2ClientAuthenticationToken clientPrincipal = OauthUtils.getAuthenticatedClientElseThrowInvalidClient(SecurityContextHolder.getContext().getAuthentication());
		RegisteredClient registeredClient = clientPrincipal.getRegisteredClient();
		if (registeredClient != null && !registeredClient.getAuthorizationGrantTypes().contains(authorizationGrantType)) {
			throw new OAuth2AuthenticationException(OAuth2ErrorCodes.INVALID_REQUEST);
		}

		MultiValueMap<String, String> parameters = getParameters(request);

		CustomAuthorizationGrantType.validate(authorizationGrantType, parameters);

		String scope = parameters.getFirst(OAuth2ParameterNames.SCOPE);
		Set<String> requestedScopes = null;
		if (StringUtils.hasText(scope)) {
			requestedScopes = new HashSet<>(
					Arrays.asList(StringUtils.delimitedListToStringArray(scope, " ")));
		}

		Map<String, Object> additionalParameters = new HashMap<>();
		parameters.forEach((key, value) -> {
			if (!key.equals(OAuth2ParameterNames.GRANT_TYPE) && !key.equals(OAuth2ParameterNames.SCOPE)) {
				additionalParameters.put(key, value.get(0));
			}
		});

		return new CustomAuthenticationToken(authorizationGrantType, clientPrincipal, requestedScopes, additionalParameters);
	}

	private static MultiValueMap<String, String> getParameters(HttpServletRequest request) {
		Map<String, String[]> parameterMap = request.getParameterMap();
		MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>(parameterMap.size());
		parameterMap.forEach((key, values) -> {
            for (String value : values) {
                parameters.add(key, value);
            }
        });
		return parameters;
	}
}
