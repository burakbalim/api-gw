package com.application.gateway.orchestration.base.sdk;

import com.application.gateway.main.common.VirtualEndpointRequestInfo;
import com.application.gateway.orchestration.base.Env;
import com.application.gateway.orchestration.base.LoggerProvider;
import org.springframework.http.ResponseEntity;

public interface VirtualEndpoint {

    ResponseEntity<Object> apply(VirtualEndpointRequestInfo requestInfo);

    void setLogger(LoggerProvider logger);

    void setEnv(Env env);
}
