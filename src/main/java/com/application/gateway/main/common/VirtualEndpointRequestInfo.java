package com.application.gateway.main.common;

import com.application.gateway.orchestration.oauth2.model.ClientType;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;

@Getter
public class VirtualEndpointRequestInfo extends RequestInfoBase {

    public VirtualEndpointRequestInfo(HttpServletRequest request, ClientType clientType) {
        super(request, clientType);
    }


}
