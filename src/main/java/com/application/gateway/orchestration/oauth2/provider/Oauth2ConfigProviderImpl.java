package com.application.gateway.orchestration.oauth2.provider;

import com.application.gateway.common.util.PathUtils;
import com.application.gateway.orchestration.ConfigurableBase;
import com.application.gateway.orchestration.common.dto.ConfigurationSourceDTO;
import com.application.gateway.orchestration.common.repository.ConfigurationProvider;
import com.application.gateway.orchestration.oauth2.config.matchers.RequestMatcherBase;
import com.application.gateway.orchestration.oauth2.model.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.stereotype.Component;

import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

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
        TokenSettings tokenSettings = getTokenSettingBuilder(clientConfiguration);

        return RegisteredClient.withId(clientConfiguration.getName())
                .clientName(clientConfiguration.getName())
                .clientId(clientConfiguration.getClientId())
                .clientSecret(clientConfiguration.getClientSecret())
                .authorizationGrantTypes(authorizationGrantTypes -> authorizationGrantTypes.addAll(clientConfiguration.getGrantType().stream().
                        map(AuthorizationGrantType::new).collect(Collectors.toSet())))
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_JWT)
                .scope(clientConfiguration.getClientType().getRole())
                .scopes(scopes -> scopes.addAll(clientConfiguration.getScopes()))
                //.redirectUris(uris -> uris.addAll(clientConfiguration.getRedirectUris()))
                .tokenSettings(tokenSettings)
                .build();
    }

    private static TokenSettings getTokenSettingBuilder(ClientConfiguration clientConfiguration) {
        TokenSettings.Builder tokenSettingBuilder = TokenSettings.builder();
        TokenUnit accessTokenUnit = clientConfiguration.getAccessTokenExp();
        TokenUnit refreshTokenUnit = clientConfiguration.getRefreshTokenExp();
        if (Objects.nonNull(accessTokenUnit)) {
            tokenSettingBuilder.accessTokenTimeToLive(accessTokenUnit.toDuration());
        }
        if (Objects.nonNull(refreshTokenUnit)) {
            tokenSettingBuilder.refreshTokenTimeToLive(refreshTokenUnit.toDuration());
        }
        tokenSettingBuilder.reuseRefreshTokens(clientConfiguration.isReuseRefreshToken());
        return tokenSettingBuilder.build();
    }
}
