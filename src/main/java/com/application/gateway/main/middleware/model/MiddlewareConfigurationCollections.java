package com.application.gateway.main.middleware.model;

import com.application.gateway.orchestration.ConfigurationBaseDTO;

import java.util.ArrayList;

public class MiddlewareConfigurationCollections extends ArrayList<SingleMiddlewareConfigurationDTO> implements ConfigurationBaseDTO {

    @Override
    public Boolean getEnable() {
        return Boolean.TRUE;
    }
}
