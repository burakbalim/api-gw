package com.application.gateway.orchestration.base.sdk;

import com.application.gateway.main.common.RequestInfoBase;
import com.application.gateway.main.common.ResponseInfo;
import com.application.gateway.orchestration.logger.Logger;
import org.springframework.context.ApplicationContext;

public interface MiddlewareFunction {

    default void applyBeforeRequest(RequestInfoBase requestInfoBase) {

    }

    default void applyAfterRequest(ResponseInfo responseInfo) {

    }

    void setLogger(Logger logger);

    void setApplicationContext(ApplicationContext applicationContext);
}
