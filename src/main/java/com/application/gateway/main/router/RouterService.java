package com.application.gateway.main.router;

import com.application.gateway.main.router.dto.RouterDTO;
import com.application.gateway.orchestration.common.dto.ConfigurationSourceDTO;
import com.application.gateway.orchestration.oauth2.model.ClientType;

public interface RouterService {

    void init(ConfigurationSourceDTO<RouterDTO> configurationSourceDTO);

    String get(String serviceName);

    boolean contains(ClientType clientType, String servicePrefix);

}
