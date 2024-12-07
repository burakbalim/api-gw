package com.application.gateway.main.custompaths;

import com.application.gateway.main.common.RequestInfoBase;
import com.application.gateway.main.common.ResponseInfo;
import com.application.gateway.main.custompaths.configuration.CustomPathsConfigurationCollections;
import com.application.gateway.main.custompaths.configuration.PathDTO;
import com.application.gateway.main.custompaths.configuration.PathRules;
import com.application.gateway.main.keymanager.KeyService;
import com.application.gateway.main.middleware.MiddlewareProvider;
import com.application.gateway.main.policies.PoliciesProvider;
import com.application.gateway.orchestration.ConfigurableBase;
import com.application.gateway.orchestration.common.dto.ConfigurationSourceDTO;
import com.application.gateway.orchestration.common.repository.ConfigurationProvider;
import com.application.gateway.orchestration.oauth2.GWAuthenticationProxy;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class CustomPathProviderImpl extends ConfigurableBase<CustomPathsConfigurationCollections> implements CustomPathProvider {

    private static final ThreadLocal<Set<String>> holdPostMiddleware = new ThreadLocal<>();

    private final KeyService keyService;

    private final MiddlewareProvider middlewareProvider;

    private final GWAuthenticationProxy gwAuthenticationProxy;

    private final PoliciesProvider policiesProvider;

    private ConfigurationSourceDTO<CustomPathsConfigurationCollections> configurationSourceDTO;

    private Map<PathDTO, CustomPathAttributeDTO> pathsConfigurationDTOMap;


    public CustomPathProviderImpl(ConfigurationProvider<CustomPathsConfigurationCollections> configurationProvider, KeyService keyService, MiddlewareProvider middlewareProvider, GWAuthenticationProxy gwAuthenticationProxy, PoliciesProvider policiesProvider) {
        super(configurationProvider);
        this.keyService = keyService;
        this.middlewareProvider = middlewareProvider;
        this.gwAuthenticationProxy = gwAuthenticationProxy;
        this.policiesProvider = policiesProvider;
    }

    @Override
    public void init(ConfigurationSourceDTO<CustomPathsConfigurationCollections> configurationSourceDTO) {
        this.configurationSourceDTO = configurationSourceDTO;
        CustomPathsConfigurationCollections configuredFile = getConfiguredFile(configurationSourceDTO);
        pathsConfigurationDTOMap = configuredFile.stream()
                .filter(item -> Objects.nonNull(item.getPathDTOS()))
                .flatMap(configurationDTO -> configurationDTO.getPathDTOS().stream()
                        .map(pathDTO -> new AbstractMap.SimpleEntry<>(new PathDTO(pathDTO.getMethod(), configurationDTO.getListenPath() + pathDTO.getEndpoint()), CustomPathAttributeDTO.from(configurationDTO))))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @Override
    public void applyBeforeRequest(RequestInfoBase requestInfoBase) {
        PathDTO pathDTO = new PathDTO(requestInfoBase.getHttpMethod().name(), requestInfoBase.getMainPath());
        if (pathsConfigurationDTOMap.containsKey(pathDTO)) {
            CustomPathAttributeDTO customPathAttributeDTO = pathsConfigurationDTOMap.get(pathDTO);
            applyRequestRules(requestInfoBase, customPathAttributeDTO);
            holdPostMiddleware.set(getPostMiddlewareIfAvailable(customPathAttributeDTO));
        }
    }

    @Override
    public void applyAfterRequest(ResponseInfo responseInfo) {
        Set<String> middlewares = holdPostMiddleware.get();
        if (Objects.nonNull(middlewares) && !middlewares.isEmpty()) {

            middlewares.stream().filter(middlewareProvider::isContains).forEach(middlewareName -> middlewareProvider.applyAfterRequest(middlewareName, responseInfo));

            holdPostMiddleware.remove();
        }
    }

    @Override
    public void onNotifyConfigurationChange() {
        init(this.configurationSourceDTO);
    }

    private void applyRequestRules(RequestInfoBase requestInfoBase, CustomPathAttributeDTO customPathAttributeDTO) {
        applyPolicies(customPathAttributeDTO);
        PathRules preMiddlewares = customPathAttributeDTO.getPreMiddlewares();
        boolean availablePreMiddlewares = Objects.nonNull(preMiddlewares) && Objects.nonNull(preMiddlewares.getMiddlewares()) && !preMiddlewares.getMiddlewares().isEmpty();

        if (availablePreMiddlewares) {
            makePreMiddlewareOperation(requestInfoBase, customPathAttributeDTO);
        }
        else {
            makeSessionOperation(requestInfoBase, customPathAttributeDTO);
        }
    }

    private void makePreMiddlewareOperation(RequestInfoBase requestInfoBase, CustomPathAttributeDTO customPathAttributeDTO) {
        PathRules pathRules = customPathAttributeDTO.getPreMiddlewares();

        if (pathRules.isRequireSession()) {
            makeSessionOperation(requestInfoBase, customPathAttributeDTO);
        }

        pathRules.getMiddlewares().stream().filter(middlewareProvider::isContains).forEach(middlewareName -> middlewareProvider.applyBeforeRequest(middlewareName, requestInfoBase));

        if (!pathRules.isRequireSession()) {
            makeSessionOperation(requestInfoBase, customPathAttributeDTO);
        }
    }

    private void makeSessionOperation(RequestInfoBase requestInfoBase, CustomPathAttributeDTO customPathAttributeDTO) {
        if (customPathAttributeDTO.isUseBasicAuth()) {
            keyService.checkPermission(customPathAttributeDTO.getApiId(), requestInfoBase);
        }
        populateWithMetaData(requestInfoBase, customPathAttributeDTO);
    }

    private void populateWithMetaData(RequestInfoBase requestInfoBase, CustomPathAttributeDTO customPathAttributeDTO) {
        if (Boolean.TRUE.equals(customPathAttributeDTO.getUseSession()) || Boolean.TRUE.equals(customPathAttributeDTO.isUseBasicAuth())) {
            gwAuthenticationProxy.populateWithMetaData(requestInfoBase);
        }
    }

    private void applyPolicies(CustomPathAttributeDTO customPathAttributeDTO) {
        if (Objects.nonNull(customPathAttributeDTO.getPoliciesName())) {
            policiesProvider.apply(customPathAttributeDTO.getPoliciesName());
        }
    }

    private Set<String> getPostMiddlewareIfAvailable(CustomPathAttributeDTO customPathAttributeDTO) {
        if (Objects.nonNull(customPathAttributeDTO.getPostMiddlewares()) && !customPathAttributeDTO.getPostMiddlewares().getMiddlewares().isEmpty()) {
            return customPathAttributeDTO.getPostMiddlewares().getMiddlewares();
        }
        return new HashSet<>();
    }
}
