package com.application.gateway.unit.orchestration.common.util.filewatcher;

import com.application.gateway.orchestration.ConfigurationBaseDTO;

public class TestConfig implements ConfigurationBaseDTO {
    private Boolean enable = true;

    @Override
    public Boolean getEnable() {
        return enable;
    }

    public void setEnable(Boolean enable) {
        this.enable = enable;
    }
} 