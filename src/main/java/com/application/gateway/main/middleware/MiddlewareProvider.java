package com.application.gateway.main.middleware;

import com.application.gateway.main.common.RequestInfoBase;
import com.application.gateway.main.common.ResponseInfo;
import com.application.gateway.main.middleware.model.MiddlewareConfigurationCollections;
import com.application.gateway.orchestration.common.dto.ConfigurationSourceDTO;

/**
 * Perform middleware for pre- and post-operations
 */
public interface MiddlewareProvider {

    void init(ConfigurationSourceDTO<MiddlewareConfigurationCollections> configurationSourceDTO);

    boolean isContains(String middlewareName);

    void applyBeforeRequest(String middlewareName, RequestInfoBase requestInfoBase);

    void applyAfterRequest(String middlewareName, ResponseInfo requestInfoBase);
}
