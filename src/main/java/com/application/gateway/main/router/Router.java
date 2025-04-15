package com.application.gateway.main.router;

import com.application.gateway.common.util.SystemEnvUtils;
import com.application.gateway.main.router.dto.RouterBaseDTO;
import com.application.gateway.main.router.dto.RouterDTO;
import com.application.gateway.orchestration.ConfigurableBase;
import com.application.gateway.orchestration.common.dto.ConfigurationSourceDTO;
import com.application.gateway.orchestration.common.repository.ConfigurationProvider;
import com.application.gateway.orchestration.oauth2.model.ClientType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
public class Router extends ConfigurableBase<RouterDTO> implements RouterService {

    private ConfigurationSourceDTO<RouterDTO> configurationSourceDTO;

    private Map<String, RouterBaseDTO> map;

    public Router(ConfigurationProvider<RouterDTO> configurationProvider) {
        super(configurationProvider);
    }

    @Override
    public void init(ConfigurationSourceDTO<RouterDTO> configurationSourceDTO) {
        this.configurationSourceDTO = configurationSourceDTO;
        RouterDTO routerDTO = getConfiguredFile(configurationSourceDTO);
        map = routerDTO.getServices().stream().collect(Collectors.toMap(RouterBaseDTO::getServiceName, this::convertedWithParsedUrl));
    }

    private RouterBaseDTO convertedWithParsedUrl(RouterBaseDTO service) {
        String parsedUrl = SystemEnvUtils.parseWithParams(service.getServiceUrl());
        service.setServiceUrl(parsedUrl);
        return service;
    }

    @Override
    public String get(String serviceName) {
        return map.get(serviceName).getServiceUrl();
    }

    @Override
    public boolean contains(ClientType clientType, String serviceName) {
        RouterBaseDTO routerBaseDTO = map.get(serviceName);
        return Objects.nonNull(routerBaseDTO) && routerBaseDTO.getAccessClient().contains(clientType);
    }

    @Override
    public void onNotifyConfigurationChange() {
        init(this.configurationSourceDTO);
    }
}
