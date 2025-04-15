package com.application.gateway.orchestration.common.util.watcher;

import com.application.gateway.orchestration.Configurable;
import com.application.gateway.orchestration.ConfigurationBaseDTO;
import com.application.gateway.orchestration.common.dto.ConfigurationSourceDTO;

public interface ConfigWatcher {

    void init();

    <T extends ConfigurationBaseDTO> void subscribe(ConfigurationSourceDTO<T> configurationSourceDTO, Configurable<T> configurable);

    void start();
}
