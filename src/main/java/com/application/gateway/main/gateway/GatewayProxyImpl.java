package com.application.gateway.main.gateway;

import com.application.gateway.common.properties.ConfigurationProperties;
import com.application.gateway.main.common.RequestInfoBase;
import com.application.gateway.main.common.ResponseInfo;
import com.application.gateway.main.custompaths.CustomPathProviderImpl;
import com.application.gateway.main.custompaths.configuration.CustomPathsConfigurationCollections;
import com.application.gateway.main.gateway.event.EventSource;
import com.application.gateway.main.middleware.model.MiddlewareConfigurationCollections;
import com.application.gateway.main.middleware.MiddlewareProviderImpl;
import com.application.gateway.main.policies.PoliciesProviderImpl;
import com.application.gateway.main.policies.model.PoliciesCollections;
import com.application.gateway.main.router.Router;
import com.application.gateway.main.router.dto.RouterDTO;
import com.application.gateway.main.virtualendpoints.VirtualEndpointProviderImpl;
import com.application.gateway.main.virtualendpoints.dto.VirtualEndpointDTOCollections;
import com.application.gateway.orchestration.common.dto.ConfigurationSourceDTO;
import com.application.gateway.orchestration.logger.Logger;
import com.application.gateway.orchestration.logger.LoggerConfiguration;
import com.application.gateway.orchestration.oauth2.model.OAuth2ConfigurationList;
import com.application.gateway.orchestration.oauth2.provider.Oauth2ConfigProviderImpl;
import com.application.gateway.orchestration.common.util.filewatcher.ConfigWatcher;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;


@Component
@AllArgsConstructor
public class GatewayProxyImpl implements GatewayProxy {

    private final ConfigurationProperties configurationProperties;

    private final ConfigWatcher configWatcher;

    private final Logger logger;

    private final Router router;

    private final Oauth2ConfigProviderImpl oauth2ConfigProvider;

    private final VirtualEndpointProviderImpl virtualEndpointProvider;

    private final MiddlewareProviderImpl middlewareProvider;

    private final CustomPathProviderImpl accessRightProvider;

    private final PoliciesProviderImpl policiesProvider;

    private final EventSource eventSource;

    /**
     * Loading of configurations when project is initializing
     */
    @Override
    public void init() {
        ConfigurationSourceDTO<LoggerConfiguration> loggerConfigPath = configurationProperties.getLoggerConfigPath();
        logger.init(loggerConfigPath);
        logger.start();

        ConfigurationSourceDTO<RouterDTO> routerConfigPath = configurationProperties.getRouterConfigPath();
        router.init(routerConfigPath);

        ConfigurationSourceDTO<OAuth2ConfigurationList> oauth2ConfigPath = configurationProperties.getOauth2ConfigPath();
        oauth2ConfigProvider.init(oauth2ConfigPath);

        ConfigurationSourceDTO<VirtualEndpointDTOCollections> virtualEndpointConfigPath = configurationProperties.getVirtualEndpointProviderNameConfigPath();
        virtualEndpointProvider.init(virtualEndpointConfigPath);

        ConfigurationSourceDTO<MiddlewareConfigurationCollections> middlewareConfigurationSourceDTO = configurationProperties.getMiddlewareProviderNameConfigPath();
        middlewareProvider.init(middlewareConfigurationSourceDTO);

        ConfigurationSourceDTO<CustomPathsConfigurationCollections> accessRightProviderNameConfigPath = configurationProperties.getAccessRightProviderNameConfigPath();
        accessRightProvider.init(accessRightProviderNameConfigPath);

        ConfigurationSourceDTO<PoliciesCollections> policyProviderNameConfigPath = configurationProperties.getPolicyProviderNameConfigPath();
        policiesProvider.init(policyProviderNameConfigPath);

        /* config watcher */
        configWatcher.subscribe(loggerConfigPath, logger);
        configWatcher.subscribe(routerConfigPath, router);
        configWatcher.subscribe(oauth2ConfigPath, oauth2ConfigProvider);
        configWatcher.subscribe(virtualEndpointConfigPath, virtualEndpointProvider);
        configWatcher.subscribe(middlewareConfigurationSourceDTO, middlewareProvider);
        configWatcher.subscribe(accessRightProviderNameConfigPath, accessRightProvider);
        configWatcher.subscribe(policyProviderNameConfigPath, policiesProvider);
        configWatcher.start();
    }

    /**
     * Perform request before reaching the point
     */
    @Override
    public void beforeRequest(RequestInfoBase requestInfo) {
        eventSource.fire(requestInfo);
    }

    /**
     * Perform request after reaching the point
     */
    @Override
    public void afterRequest(ResponseInfo responseInfo) {
        eventSource.fire(responseInfo);
    }
}
