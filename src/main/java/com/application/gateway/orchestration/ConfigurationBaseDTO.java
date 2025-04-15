package com.application.gateway.orchestration;

import java.io.Serializable;

/**
 * Represents a base interface for configuration data objects that can be serialized.
 */
public interface ConfigurationBaseDTO extends Serializable {

    /**
     * Gets the enable status of the configuration.
     * By default, it returns true.
     *
     * @return The enable status of the configuration.
     */
    default Boolean getEnable() {
        return Boolean.TRUE;
    }
}