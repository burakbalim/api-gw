package com.application.gateway.orchestration.oauth2.config;

import com.application.gateway.orchestration.common.dto.ConfigurationSourceDTO;
import com.application.gateway.orchestration.oauth2.config.matchers.AllowedPathsRequestMatcher;
import com.application.gateway.orchestration.oauth2.config.matchers.AppRequestMatcher;
import com.application.gateway.orchestration.oauth2.config.matchers.PortalRequestMatcher;
import com.application.gateway.orchestration.oauth2.config.matchers.ThirdPartyRequestMatcher;
import com.application.gateway.orchestration.oauth2.config.mixin.LongMixin;
import com.application.gateway.orchestration.oauth2.config.mixin.UserDTOMixin;
import com.application.gateway.orchestration.oauth2.customtoken.CustomAuthenticationConverter;
import com.application.gateway.orchestration.oauth2.customtoken.CustomAuthenticationProvider;
import com.application.gateway.orchestration.oauth2.model.User;
import com.application.gateway.orchestration.oauth2.registeredclient.RegisteredClientRepositoryImpl;
import com.application.gateway.orchestration.oauth2.service.GwAuthorizationService;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.jackson2.SecurityJackson2Modules;
import org.springframework.security.oauth2.core.OAuth2Token;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.authorization.JdbcOAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2ClientAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.jackson2.OAuth2AuthorizationServerJackson2Module;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.token.*;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.springframework.security.config.Customizer.withDefaults;
import static org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames.ACCESS_TOKEN;
import static org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames.REFRESH_TOKEN;

/**
 * Perform OAuth2 mechanism settings
 */
@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
@Slf4j
@SuppressWarnings("deprecation")
public class WebSecurityConfig {

    private final ApplicationContext applicationContext;

    private final AppRequestMatcher appRequestMatcher;

    private final PortalRequestMatcher portalRequestMatcher;

    private final ThirdPartyRequestMatcher thirdPartyRequestMatcher;

    private final AllowedPathsRequestMatcher allowedPathsRequestMatcher;

    private final JdbcTemplate jdbcTemplate;

    private final JwtAuthenticationEntryPoint authenticationEntryPoint;

    /**
     * The bean build oauth2ResourceServer with customization token endpoint that open new grant type for specification
     * HttpSecurity build for httpSecurity
     *
     * @return Security Filter chain that build oauth2ResourceServer
     * @throws Exception when building resource server
     */
    @Bean
    @Order(2)
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http);
        return http
                .getConfigurer(OAuth2AuthorizationServerConfigurer.class)
                .tokenEndpoint(tokenEndpoint -> tokenEndpoint
                        .accessTokenRequestConverter(new CustomAuthenticationConverter())
                        .authenticationProvider(new CustomAuthenticationProvider(gwAuthorizationService(), applicationContext))
                )
                .oidc(withDefaults())
                .and()
                .oauth2ResourceServer(httpSecurityOAuth2ResourceServerConfigurer -> httpSecurityOAuth2ResourceServerConfigurer.jwt(Customizer.withDefaults()))
                .build();
    }

    /**
     * Bean definition for providing a SecurityFilterChain
     * The chain use secured and permitted path via configured matcher from Oauth2ConfigProvider.class
     *
     * @see com.application.gateway.orchestration.oauth2.provider.Oauth2ConfigProvider#init(ConfigurationSourceDTO)
     * @return Security Filter chain that has secured and permitted paths
     */
    @Bean
    @Order(2)
    public SecurityFilterChain securityFilterChainAs(HttpSecurity http) throws Exception {
        return http.
                formLogin(AbstractHttpConfigurer::disable).
                cors(AbstractHttpConfigurer::disable).
                csrf(AbstractHttpConfigurer::disable).
                authorizeHttpRequests(authorizationManagerRequestMatcherRegistry ->
                        authorizationManagerRequestMatcherRegistry.
                                requestMatchers(allowedPathsRequestMatcher).permitAll().
                                requestMatchers(appRequestMatcher).authenticated().
                                requestMatchers(portalRequestMatcher).authenticated().
                                requestMatchers(thirdPartyRequestMatcher).authenticated().
                                anyRequest().permitAll()
                ).exceptionHandling(ex -> ex.authenticationEntryPoint(authenticationEntryPoint)).
                build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }

    @Bean
    public JwtDecoder jwtDecoder(JWKSource<SecurityContext> jwkSource) {
        return OAuth2AuthorizationServerConfiguration.jwtDecoder(jwkSource);
    }

    /**
     * Bean definition for providing a repository of registered OAuth2 clients.
     *
     * @return A repository containing a single registered client.
     */
    @Bean
    public RegisteredClientRepository registeredClientRepository() {
        return new RegisteredClientRepositoryImpl();
    }

    /**
     * Bean definition for providing a JSON Web Key (JWK) source for OAuth2 JWT verification.
     *
     * @return A JWK source configured based on the key-store.json file.
     * @throws RuntimeException if an error occurs while loading or parsing the key-store.json file.
     */
    @Bean
    public JWKSource<SecurityContext> jwkSource() {
        JWKSet set;
        try {
            set = JWKSet.load(Objects.requireNonNull(getClass().getResourceAsStream("/keys/key-store.json")));
            JWKSet finalSet = set;
            return (j, sc) -> j.select(finalSet);
        } catch (IOException | ParseException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Bean definition for providing authorization server setting like authentication links
     *
     */
    @Bean
    public AuthorizationServerSettings authorizationServerSettings() {
        return AuthorizationServerSettings.builder().build();
    }

    /**
     * Bean definition for providing an OAuth2 Authorization Service.
     * Configured with jdbcTemplate using mixin class to deserialize from table fields
     *
     * @return An OAuth2 Authorization service configured with JWT encoder and customizers.
     */
    @Bean
    public OAuth2AuthorizationService authorizationService() {
        JdbcOAuth2AuthorizationService authorizationService =
                new JdbcOAuth2AuthorizationService(jdbcTemplate, registeredClientRepository());
        JdbcOAuth2AuthorizationService.OAuth2AuthorizationRowMapper rowMapper =
                new JdbcOAuth2AuthorizationService.OAuth2AuthorizationRowMapper(registeredClientRepository());
        JdbcOAuth2AuthorizationService.OAuth2AuthorizationParametersMapper oAuth2AuthorizationParametersMapper =
                new JdbcOAuth2AuthorizationService.OAuth2AuthorizationParametersMapper();

        ObjectMapper objectMapper = new ObjectMapper();
        ClassLoader classLoader = JdbcOAuth2AuthorizationService.class.getClassLoader();
        List<Module> securityModules = SecurityJackson2Modules.getModules(classLoader);
        objectMapper.registerModules(securityModules);
        objectMapper.registerModule(new OAuth2AuthorizationServerJackson2Module());
        objectMapper.addMixIn(OAuth2ClientAuthenticationToken.class, OAuth2ClientAuthenticationTokenMixin.class);
        objectMapper.addMixIn(User.class, UserDTOMixin.class);
        objectMapper.addMixIn(Long.class, LongMixin.class);

        rowMapper.setObjectMapper(objectMapper);
        oAuth2AuthorizationParametersMapper.setObjectMapper(objectMapper);

        authorizationService.setAuthorizationRowMapper(rowMapper);
        authorizationService.setAuthorizationParametersMapper(oAuth2AuthorizationParametersMapper);

        return authorizationService;
    }

    /**
     * Bean definition for providing an OAuth2 token generator.
     *
     * @return An OAuth2 token generator configured with JWT encoder and customizers.
     */
    @Bean
    public OAuth2TokenGenerator<? extends OAuth2Token> tokenGenerator() {
        NimbusJwtEncoder jwtEncoder = new NimbusJwtEncoder(jwkSource());
        JwtGenerator jwtGenerator = new JwtGenerator(jwtEncoder);
        jwtGenerator.setJwtCustomizer(tokenCustomizer());
        OAuth2AccessTokenGenerator accessTokenGenerator = new OAuth2AccessTokenGenerator();
        OAuth2RefreshTokenGenerator refreshTokenGenerator = new OAuth2RefreshTokenGenerator();
        return new DelegatingOAuth2TokenGenerator(
                jwtGenerator, accessTokenGenerator, refreshTokenGenerator);
    }

    /**
     * Bean definition for providing an OAuth2 token customizer.
     *
     * @return An OAuth2 token customizer
     */
    @Bean
    public OAuth2TokenCustomizer<JwtEncodingContext> tokenCustomizer() {
        return context -> {
            OAuth2ClientAuthenticationToken principal = context.getPrincipal();
            RegisteredClient registeredClient = principal.getRegisteredClient();
            if (Objects.isNull(registeredClient)) {
                return;
            }
            if (ACCESS_TOKEN.equals(context.getTokenType().getValue()) || REFRESH_TOKEN.equals(context.getTokenType().getValue())) {
                log.info("Token issued for grant: {}", context.getAuthorizationGrantType().getValue());

                Set<String> roles = registeredClient.getScopes().stream()
                        .filter(scope -> scope.startsWith("ROLE_"))
                        .collect(Collectors.toSet());
                JwtClaimsSet.Builder claims = context.getClaims();
                claims.claim("authorities", roles);

                if (principal.getDetails() instanceof User user) {
                    claims.claims(claimsList -> {
                        Optional.ofNullable(user.getUsername()).ifPresent(value -> claimsList.put("username", value));
                        Optional.ofNullable(user.getId()).ifPresent(value -> claimsList.put("id", value));
                        Optional.ofNullable(user.getEmail()).ifPresent(value -> claimsList.put("email", value));
                        Optional.ofNullable(user.getAuthProvider()).ifPresent(value -> claimsList.put("auth_provider", value));
                        Optional.ofNullable(user.getBirthDate()).ifPresent(value -> claimsList.put("birth_date", value));
                        Optional.ofNullable(user.getExternalId()).ifPresent(value -> claimsList.put("external_id", value));
                    });
                }
            }
        };
    }

    @Bean
    public GwAuthorizationService gwAuthorizationService() {
        return new GwAuthorizationService(registeredClientRepository(), tokenGenerator(), authorizationService(), authorizationServerSettings());
    }


    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("*"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
