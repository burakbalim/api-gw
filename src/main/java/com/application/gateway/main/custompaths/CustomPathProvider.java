package com.application.gateway.main.custompaths;

import com.application.gateway.main.common.RequestInfoBase;
import com.application.gateway.main.common.ResponseInfo;
import com.application.gateway.main.custompaths.configuration.CustomPathsConfigurationCollections;
import com.application.gateway.orchestration.common.dto.ConfigurationSourceDTO;

/**
 * This interface represents a custom path provider that can perform functions via configuration
 */
public interface CustomPathProvider {

    void init(ConfigurationSourceDTO<CustomPathsConfigurationCollections> configurationSourceDTO);

    void applyBeforeRequest(RequestInfoBase requestInfoBase);

    void applyAfterRequest(ResponseInfo requestInfoBase);
}
