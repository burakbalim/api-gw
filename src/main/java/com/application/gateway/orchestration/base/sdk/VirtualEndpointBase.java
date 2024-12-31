package com.application.gateway.orchestration.base.sdk;

import com.application.gateway.main.common.RequestInfoBase;
import com.application.gateway.orchestration.base.Env;
import com.application.gateway.orchestration.base.LoggerProvider;

import java.io.IOException;
import java.util.Map;

public abstract class VirtualEndpointBase implements VirtualEndpoint {

    protected LoggerProvider logger;

    protected Env env;

    @Override
    public void setLogger(LoggerProvider logger) {
        this.logger = logger;
    }

    @Override
    public void setEnv(Env env) {
        this.env = env;
    }

    public Map<String, Object> getBody(RequestInfoBase requestInfoBase) {
        try {
            return HttpBodyExecutor.extractBody(requestInfoBase.cloneHttpEntity());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public LoggerProvider getLogger() {
        return logger;
    }

    public Env getEnv() {
        return env;
    }
}
