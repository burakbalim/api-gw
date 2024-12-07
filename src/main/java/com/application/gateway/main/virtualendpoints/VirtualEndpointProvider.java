package com.application.gateway.main.virtualendpoints;

import com.application.gateway.main.common.VirtualEndpointRequestInfo;
import com.application.gateway.main.virtualendpoints.dto.VirtualEndpointDTOCollections;
import com.application.gateway.orchestration.common.dto.ConfigurationSourceDTO;
import org.springframework.http.ResponseEntity;

public interface VirtualEndpointProvider {

    void init(ConfigurationSourceDTO<VirtualEndpointDTOCollections> configurationSourceDTO);

    boolean isContains(String path);

    ResponseEntity<Object> request(VirtualEndpointRequestInfo virtualRequestInfo);
}
