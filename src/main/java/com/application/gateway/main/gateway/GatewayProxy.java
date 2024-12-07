package com.application.gateway.main.gateway;

import com.application.gateway.main.common.RequestInfoBase;
import com.application.gateway.main.common.ResponseInfo;

public interface GatewayProxy {

    void init();

    void beforeRequest(RequestInfoBase requestInfo);

    void afterRequest(ResponseInfo responseInfo);
}
