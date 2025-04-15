package com.application.gateway.main.common;

import com.application.gateway.orchestration.oauth2.model.ClientType;
import jakarta.servlet.http.HttpServletRequest;

public class UndefinedRequestInfo extends InternalRequestInfo {

    public UndefinedRequestInfo(HttpServletRequest request, ClientType clientType) {
        super(request, clientType);
    }
}
