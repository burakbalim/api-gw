package com.application.gateway.orchestration.oauth2.provider;

import com.application.gateway.common.util.Constants;
import com.application.gateway.common.util.PathUtils;
import com.application.gateway.orchestration.ConfigurableBase;
import com.application.gateway.orchestration.common.dto.ConfigurationSourceDTO;
import com.application.gateway.orchestration.common.repository.ConfigurationProvider;
import com.application.gateway.orchestration.oauth2.config.matchers.RequestMatcherBase;
import com.application.gateway.orchestration.oauth2.model.ClientConfiguration;
import com.application.gateway.orchestration.oauth2.model.ClientType;
import com.application.gateway.orchestration.oauth2.model.OAuth2ConfigurationList;
import com.application.gateway.orchestration.oauth2.model.PathConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.stereotype.Component;

import java.security.Principal;
import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.security.oauth2.core.oidc.OidcScopes.OPENID;
import static org.springframework.security.oauth2.core.oidc.OidcScopes.PROFILE;

@Component
@Slf4j
public class Oauth2ConfigProviderImpl extends ConfigurableBase<OAuth2ConfigurationList> implements Oauth2ConfigProvider {

    private final RegisteredClientRepository registeredClientRepository;

    private final Map<ClientType, RequestMatcherBase> requestMatcherMap;

    private OAuth2ConfigurationList configuration;

    private ConfigurationSourceDTO<OAuth2ConfigurationList> configurationSourceDTO;

    private Map<String, ClientConfiguration> oauth2Map;

    private Map<ClientType, List<String>> clientTypePathConfigurations = null;

    public Oauth2ConfigProviderImpl(ApplicationContext applicationContext, ConfigurationProvider<OAuth2ConfigurationList> configurationProvider,
                                    RegisteredClientRepository registeredClientRepository) {
        super(configurationProvider);
        this.registeredClientRepository = registeredClientRepository;
        this.requestMatcherMap = applicationContext.getBeanProvider(RequestMatcherBase.class).stream().
                collect(Collectors.toMap(RequestMatcherBase::getType, externalService -> externalService));
    }


    @Override
    public void init(ConfigurationSourceDTO<OAuth2ConfigurationList> configurationSourceDTO) {
        this.configurationSourceDTO = configurationSourceDTO;
        configuration = getConfiguredFile(configurationSourceDTO);
        List<PathConfiguration> pathConfigurations = configuration.getPathConfigurations();
        clientTypePathConfigurations = pathConfigurations.stream().collect(Collectors.toMap(PathConfiguration::getType, PathConfiguration::getPaths));
        pathConfigurations.forEach(pathConfiguration -> requestMatcherMap.get(pathConfiguration.getType()).setPaths(pathConfiguration.getPaths()));
        oauth2Map = configuration.getConfigurations().stream().collect(Collectors.toMap(ClientConfiguration::getClientId, config -> config));
        addClientToSecurityManager();
        log.info("Initializing completed for " + getClass().getName());
    }

    @Override
    protected void onNotifyConfigurationChange() {
        init(this.configurationSourceDTO);
    }

    @Override
    public Map<String, String> getMetaData(String clientId) {
        ClientConfiguration singleConfiguration = oauth2Map.get(clientId);
        if (Objects.isNull(singleConfiguration)) {
            return new HashMap<>();
        }
        return Map.of("third_party_source", singleConfiguration.getName());
    }

    @Override
    public Optional<ClientType> getClientType(Principal principal, String mainPath) {
        ClientConfiguration clientConfiguration = Objects.nonNull(principal) ? oauth2Map.get(principal.getName()) : null;
        if (clientConfiguration != null) {
            return Optional.of(clientConfiguration.getClientType());
        } else if (Objects.nonNull(clientTypePathConfigurations.get(ClientType.ALLOWED_PATHS)) && anyMatchWithAllowedPaths(mainPath)) {
            return Optional.of(ClientType.ALLOWED_PATHS);
        }
        return Optional.empty();
    }

    private boolean anyMatchWithAllowedPaths(String mainPath) {
        List<String> allowedPaths = clientTypePathConfigurations.get(ClientType.ALLOWED_PATHS);
        return PathUtils.isPathMatch(allowedPaths, mainPath);
    }

    private void addClientToSecurityManager() {
        configuration.getConfigurations().forEach(clientConfiguration -> {
            RegisteredClient registeredClient = getRegisteredClient(clientConfiguration);
            registeredClientRepository.save(registeredClient);
        });
    }

    private RegisteredClient getRegisteredClient(ClientConfiguration clientConfiguration) {
        TokenSettings.Builder tokenSettingBuilder = TokenSettings.builder();
        if (Objects.nonNull(clientConfiguration.getAccessTokenExp())) {
            tokenSettingBuilder.accessTokenTimeToLive(Duration.ofMinutes(clientConfiguration.getAccessTokenExp()));
        }
        if (Objects.nonNull(clientConfiguration.getRefreshTokenExp())) {
            tokenSettingBuilder.refreshTokenTimeToLive(Duration.ofMinutes(clientConfiguration.getRefreshTokenExp()));
        }
        RegisteredClient.Builder builder = RegisteredClient.withId(clientConfiguration.getClientId());
        if (clientConfiguration.getGrantType().equals(AuthorizationGrantType.REFRESH_TOKEN.getValue())) {
            builder.authorizationGrantType(new AuthorizationGrantType(Constants.CUSTOM_PASSWORD_GRANT_TYPE));
        }
        return builder
                .clientName(clientConfiguration.getName())
                .clientId(clientConfiguration.getClientId())
                .clientSecret(clientConfiguration.getClientSecret())
                .authorizationGrantType(new AuthorizationGrantType(clientConfiguration.getGrantType()))
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_JWT)
                .scope(clientConfiguration.getClientType().getRole())
                .scope(OPENID)
                .scope(PROFILE)
                .tokenSettings(tokenSettingBuilder.build())
                .build();
    }
}
