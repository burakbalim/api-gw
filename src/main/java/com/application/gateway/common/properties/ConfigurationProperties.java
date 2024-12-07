package com.application.gateway.common.properties;

import com.application.gateway.common.ConfigurationProviderType;
import com.application.gateway.main.custompaths.configuration.CustomPathsConfigurationCollections;
import com.application.gateway.main.middleware.model.MiddlewareConfigurationCollections;
import com.application.gateway.main.policies.model.PoliciesCollections;
import com.application.gateway.main.router.dto.RouterDTO;
import com.application.gateway.main.virtualendpoints.dto.VirtualEndpointDTOCollections;
import com.application.gateway.orchestration.common.dto.ConfigurationSourceDTO;
import com.application.gateway.orchestration.logger.LoggerConfiguration;
import com.application.gateway.orchestration.oauth2.model.OAuth2ConfigurationList;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConfigurationProperties {

    @Value("${configuration.provider}")
    private ConfigurationProviderType configurationProviderType;

    @Value("${configuration.file.path}")
    private String configFilePath;

    @Value("${configuration.logger.name}")
    private String loggerName;

    @Value("${configuration.router.name}")
    private String routerName;

    @Value("${configuration.oauth2.name}")
    private String oauth2Name;

    @Value("${configuration.virtual_endpoint.name}")
    private String virtualEndpointProviderName;

    @Value("${configuration.middlewares.name}")
    private String middlewareProviderConfigName;

    @Value("${configuration.accessright.name}")
    private String accessRightProviderConfigName;

    @Value("${configuration.policy.name}")
    private String policyConfigName;

    public ConfigurationSourceDTO<LoggerConfiguration> getLoggerConfigPath() {
        String configName = getConfigurationName(loggerName);
        return new ConfigurationSourceDTO<>(configName, getConfigurationSource(configName), LoggerConfiguration.class);
    }

    public ConfigurationSourceDTO<RouterDTO> getRouterConfigPath() {
        String configName = getConfigurationName(routerName);
        return new ConfigurationSourceDTO<>(configName, getConfigurationSource(configName), RouterDTO.class);
    }

    public ConfigurationSourceDTO<OAuth2ConfigurationList> getOauth2ConfigPath() {
        String configName = getConfigurationName(oauth2Name);
        return new ConfigurationSourceDTO<>(configName, getConfigurationSource(configName), OAuth2ConfigurationList.class);
    }

    public ConfigurationSourceDTO<VirtualEndpointDTOCollections> getVirtualEndpointProviderNameConfigPath() {
        String configName = getConfigurationName(virtualEndpointProviderName);
        return new ConfigurationSourceDTO<>(configName, getConfigurationSource(configName), VirtualEndpointDTOCollections.class);
    }

    public ConfigurationSourceDTO<MiddlewareConfigurationCollections> getMiddlewareProviderNameConfigPath() {
        String configName = getConfigurationName(middlewareProviderConfigName);
        return new ConfigurationSourceDTO<>(configName, getConfigurationSource(configName), MiddlewareConfigurationCollections.class);
    }

    public ConfigurationSourceDTO<CustomPathsConfigurationCollections> getAccessRightProviderNameConfigPath() {
        String configName = getConfigurationName(accessRightProviderConfigName);
        return new ConfigurationSourceDTO<>(configName, getConfigurationSource(configName), CustomPathsConfigurationCollections.class);
    }

    public ConfigurationSourceDTO<PoliciesCollections> getPolicyProviderNameConfigPath() {
        String configName = getConfigurationName(policyConfigName);
        return new ConfigurationSourceDTO<>(configName, getConfigurationSource(configName), PoliciesCollections.class);
    }

    private String getConfigurationName(String configName) {
        return ConfigurationProviderType.getFileName(configurationProviderType, () -> configName);
    }

    private String getConfigurationSource(String configName) {
        return ConfigurationProviderType.FILE.equals(configurationProviderType) ? configFilePath + configName : configName;
    }
}
