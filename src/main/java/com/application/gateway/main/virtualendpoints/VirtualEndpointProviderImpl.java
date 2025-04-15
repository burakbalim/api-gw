package com.application.gateway.main.virtualendpoints;

import com.application.gateway.common.util.PathUtils;
import com.application.gateway.main.common.VirtualEndpointRequestInfo;
import com.application.gateway.main.common.util.ExternalClassDetector;
import com.application.gateway.main.middleware.MiddlewareProvider;
import com.application.gateway.main.virtualendpoints.dto.VirtualEndpointDTO;
import com.application.gateway.main.virtualendpoints.dto.VirtualEndpointDTOCollections;
import com.application.gateway.orchestration.ConfigurableBase;
import com.application.gateway.orchestration.base.Env;
import com.application.gateway.orchestration.base.sdk.VirtualEndpoint;
import com.application.gateway.orchestration.common.dto.ConfigurationSourceDTO;
import com.application.gateway.orchestration.common.repository.ConfigurationProvider;
import com.application.gateway.orchestration.logger.Logger;
import com.application.gateway.orchestration.oauth2.GWAuthenticationProxy;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class VirtualEndpointProviderImpl extends ConfigurableBase<VirtualEndpointDTOCollections> implements VirtualEndpointProvider {

    private final MiddlewareProvider middlewareProvider;

    private final ExternalClassDetector<VirtualEndpoint> externalClassDetector;

    private final GWAuthenticationProxy gwAuthenticationProxy;

    private final Logger logger;

    private final Environment environment;

    private Map<String, VirtualEndpoint> virtualEndpointMap;

    private Map<String, VirtualEndpointDTO> virtualEndpointConfigMap;

    private ConfigurationSourceDTO<VirtualEndpointDTOCollections> configurationSourceDTO;


    public VirtualEndpointProviderImpl(ConfigurationProvider<VirtualEndpointDTOCollections> configurationProvider, MiddlewareProvider middlewareProvider,
                                       ExternalClassDetector<VirtualEndpoint> externalClassDetector, GWAuthenticationProxy gwAuthenticationProxy, Logger logger,
                                       Environment environment) {
        super(configurationProvider);
        this.middlewareProvider = middlewareProvider;
        this.externalClassDetector = externalClassDetector;
        this.gwAuthenticationProxy = gwAuthenticationProxy;
        this.logger = logger;
        this.environment = environment;
    }

    @Override
    public void init(ConfigurationSourceDTO<VirtualEndpointDTOCollections> configurationSourceDTO) {
        this.configurationSourceDTO = configurationSourceDTO;
        List<VirtualEndpointDTO> configuredFiles = getConfiguredFile(configurationSourceDTO);
        this.virtualEndpointMap = configuredFiles.stream().collect(Collectors.toMap(VirtualEndpointDTO::getPath, this::getVirtualEndpoint, (existingValue, newValue) -> existingValue, HashMap::new));
        this.virtualEndpointConfigMap = configuredFiles.stream().collect(Collectors.toMap(VirtualEndpointDTO::getPath, configuration -> configuration, (existingValue, newValue) -> existingValue, HashMap::new));
    }

    @Override
    public boolean isContains(String path) {
        path = PathUtils.convertPathFromSlash(path);
        return virtualEndpointMap.containsKey(path);
    }

    @Override
    public ResponseEntity<Object> request(VirtualEndpointRequestInfo virtualRequestInfo) {
        String mainPath = PathUtils.convertPathFromSlash(virtualRequestInfo.getMainPath());
        VirtualEndpoint virtualEndpoint = virtualEndpointMap.get(mainPath);
        VirtualEndpointDTO virtualEndpointDTO = virtualEndpointConfigMap.get(mainPath);
        applyMiddlewares(virtualRequestInfo, virtualEndpointDTO);
        populateMetaData(virtualRequestInfo, virtualEndpointDTO);
        return virtualEndpoint.apply(virtualRequestInfo);
    }

    @Override
    public void onNotifyConfigurationChange() {
        init(this.configurationSourceDTO);
    }

    private VirtualEndpoint getVirtualEndpoint(VirtualEndpointDTO virtualEndpointDTO) {
        Optional<Object> optionalBean = externalClassDetector.getBean(virtualEndpointDTO.getClassName());
        VirtualEndpoint virtualEndpoint = optionalBean.map(VirtualEndpoint.class::cast).orElseGet(() -> externalClassDetector.getInstanceOfClass(virtualEndpointDTO.getClassName()));
        virtualEndpoint.setLogger(logger);
        virtualEndpoint.setEnv(Env.from(environment));
        return virtualEndpoint;
    }

    private void applyMiddlewares(VirtualEndpointRequestInfo virtualRequestInfo, VirtualEndpointDTO virtualEndpointDTO) {
        Set<String> middlewares = virtualEndpointDTO.getMiddlewares();
        if (Objects.nonNull(middlewares)) {
            middlewares.stream().filter(middlewareProvider::isContains).forEach(middlewareName -> middlewareProvider.applyBeforeRequest(middlewareName, virtualRequestInfo));
        }
    }

    private void populateMetaData(VirtualEndpointRequestInfo virtualRequestInfo, VirtualEndpointDTO virtualEndpointDTO) {
        if (Boolean.TRUE.equals(virtualEndpointDTO.getUseSession())) {
            gwAuthenticationProxy.populateWithMetaData(virtualRequestInfo);
        }
    }
}
