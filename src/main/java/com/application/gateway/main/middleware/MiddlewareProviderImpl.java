package com.application.gateway.main.middleware;

import com.application.gateway.main.common.RequestInfoBase;
import com.application.gateway.main.common.ResponseInfo;
import com.application.gateway.main.common.util.ExternalClassDetector;
import com.application.gateway.orchestration.base.sdk.MiddlewareFunction;
import com.application.gateway.main.middleware.model.*;
import com.application.gateway.orchestration.ConfigurableBase;
import com.application.gateway.orchestration.common.dto.ConfigurationSourceDTO;
import com.application.gateway.orchestration.common.repository.ConfigurationProvider;
import com.application.gateway.orchestration.logger.Logger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@Slf4j
public class MiddlewareProviderImpl extends ConfigurableBase<MiddlewareConfigurationCollections> implements MiddlewareProvider {

    private final ExternalClassDetector<MiddlewareFunction> externalClassDetector;
    private final Logger logger;
    private final UrlWriterService urlWriterService;
    private final ApplicationContext applicationContext;
    private ConfigurationSourceDTO<MiddlewareConfigurationCollections> configurationSourceDTO;
    private Map<String, MiddlewareFunction> middlewareBeforeRequestFunctionMap = new HashMap<>();
    private Map<String, MiddlewareFunction> middlewareAfterRequestFunctionMap = new HashMap<>();
    private Map<String, SingleMiddlewareConfigurationDTO> singleMiddlewareConfigurationDTOMap = new HashMap<>();

    public MiddlewareProviderImpl(ApplicationContext applicationContext, ConfigurationProvider<MiddlewareConfigurationCollections> configurationProvider, ExternalClassDetector<MiddlewareFunction> externalClassDetector, Logger logger, UrlWriterService urlWriterService) {
        super(configurationProvider);
        this.externalClassDetector = externalClassDetector;
        this.logger = logger;
        this.urlWriterService = urlWriterService;
        this.applicationContext = applicationContext;
    }

    @Override
    public void init(ConfigurationSourceDTO<MiddlewareConfigurationCollections> configurationSourceDTO) {
        this.configurationSourceDTO = configurationSourceDTO;
        MiddlewareConfigurationCollections middlewareConfigurationCollections = getConfiguredFile(configurationSourceDTO);
        middlewareBeforeRequestFunctionMap = middlewareConfigurationCollections.stream().filter(item -> Objects.nonNull(item.getBeforeRequest())).collect(Collectors.toMap(SingleMiddlewareConfigurationDTO::getName, x -> getCustomFunction(x.getBeforeRequest().getCustomFunctionName())));
        middlewareAfterRequestFunctionMap = middlewareConfigurationCollections.stream().filter(item -> Objects.nonNull(item.getAfterRequest())).collect(Collectors.toMap(SingleMiddlewareConfigurationDTO::getName, x -> getCustomFunction(x.getAfterRequest().getCustomFunctionName())));
        singleMiddlewareConfigurationDTOMap = middlewareConfigurationCollections.stream().collect(Collectors.toMap(SingleMiddlewareConfigurationDTO::getName, item -> item));
    }

    @Override
    public boolean isContains(String middlewareName) {
        return singleMiddlewareConfigurationDTOMap.containsKey(middlewareName);
    }

    @Override
    public void applyBeforeRequest(String middlewareName, RequestInfoBase requestInfoBase) {
        applyBeforeCustomFunction(middlewareName, requestInfoBase);
        applyExtendedPath(middlewareName, requestInfoBase);
    }

    @Override
    public void applyAfterRequest(String middlewareName, ResponseInfo requestInfoBase) {
        if (middlewareAfterRequestFunctionMap.containsKey(middlewareName)) {
            middlewareAfterRequestFunctionMap.get(middlewareName).applyAfterRequest(requestInfoBase);
        }
    }

    @Override
    protected void onNotifyConfigurationChange() {
        init(this.configurationSourceDTO);
    }

    private void applyBeforeCustomFunction(String middlewareName, RequestInfoBase requestInfoBase) {
        if (middlewareBeforeRequestFunctionMap.containsKey(middlewareName)) {
            middlewareBeforeRequestFunctionMap.get(middlewareName).applyBeforeRequest(requestInfoBase);
        }
    }

    private void applyExtendedPath(String middlewareName, RequestInfoBase requestInfoBase) {
        ExtendedPath extendedPath = singleMiddlewareConfigurationDTOMap.get(middlewareName).getExtendedPath();
        if (Objects.nonNull(extendedPath)) {
            applyTransformHeaderRule(requestInfoBase, extendedPath);
            applyUrlRewritesRule(requestInfoBase, extendedPath);
            changeMediaType(requestInfoBase, extendedPath);
        }
    }

    private void changeMediaType(RequestInfoBase requestInfoBase, ExtendedPath extendedPath) {
        if (Objects.nonNull(extendedPath.getMediaType())) {
            requestInfoBase.changeMediaType(MediaType.valueOf(extendedPath.getMediaType()));
        }
    }

    private void applyTransformHeaderRule(RequestInfoBase requestInfoBase, ExtendedPath extendedPath) {
        List<TransformHeader> transformHeaders = extendedPath.getTransformHeader();
        if (Objects.nonNull(transformHeaders)) {
            transformHeaders.stream().filter(transformHeader -> transformHeader.getMethod().equals(requestInfoBase.getHttpMethod().name()) && Pattern.compile(transformHeader.getPath()).matcher(requestInfoBase.getUri()).find()).forEach(singleExtendedPath -> {
                requestInfoBase.removeHeaders(singleExtendedPath.getDeleteHeaders());
                requestInfoBase.addHeaders(singleExtendedPath.getAddHeaders());
            });
            requestInfoBase.rearrangeHttpEntity();
        }
    }

    private void applyUrlRewritesRule(RequestInfoBase requestInfoBase, ExtendedPath extendedPath) {
        List<UrlRewrites> urlRewrites = extendedPath.getUrlRewrites();
        if (Objects.nonNull(urlRewrites)) {
            urlRewrites.stream().filter(urlRewrite -> urlWriterService.isMatchingWithRule(urlRewrite, requestInfoBase.getUri(), requestInfoBase.getHttpMethod().name())).forEach(urlRewrite ->
                    requestInfoBase.addUri(urlWriterService.rewriteUrl(urlRewrite, requestInfoBase.getUri())));
        }
    }

    private MiddlewareFunction getCustomFunction(String functionName) {
        MiddlewareFunction middlewareFunction = externalClassDetector.getInstanceOfClass(functionName);
        middlewareFunction.setLogger(logger);
        middlewareFunction.setApplicationContext(applicationContext);
        return middlewareFunction;
    }
}
