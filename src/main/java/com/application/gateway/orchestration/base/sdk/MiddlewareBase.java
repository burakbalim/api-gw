package com.application.gateway.orchestration.base.sdk;

import com.application.gateway.main.common.RequestInfoBase;
import com.application.gateway.orchestration.logger.Logger;
import org.springframework.context.ApplicationContext;

import java.io.IOException;
import java.util.Map;

public abstract class MiddlewareBase implements MiddlewareFunction {

    protected Logger logger;

    protected ApplicationContext applicationContext;

    @Override
    public void setLogger(Logger logger){
        this.logger = logger;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public Map<String, Object> getBody(RequestInfoBase requestInfoBase) {
        try {
            return HttpBodyExecutor.extractBody(requestInfoBase.cloneHttpEntity());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public <T> T getBean(Class<T> t) {
        return applicationContext.getBean(t);
    }

    public Logger getLogger() {
        return logger;
    }

    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }
}
