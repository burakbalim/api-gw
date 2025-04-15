package com.application.gateway.orchestration.oauth2.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.lang.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AuthorizationGrantAuthenticationToken;

import java.io.Serial;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Getter
@EqualsAndHashCode(callSuper = true)
public class CustomAuthenticationToken extends OAuth2AuthorizationGrantAuthenticationToken {

	@Serial
	private static final long serialVersionUID = 1L;
	private final Map<String, Object> parameters;
	private final Set<String> scopes;

	public CustomAuthenticationToken(AuthorizationGrantType authorizationGrantType, Authentication clientPrincipal,
									 @Nullable Set<String> scopes, @Nullable Map<String, Object> additionalParameters) {
		super(authorizationGrantType, clientPrincipal, additionalParameters);
		this.parameters = additionalParameters;
		this.scopes = Collections.unmodifiableSet(
				scopes != null ? new HashSet<>(scopes) : Collections.emptySet());
	}

}
