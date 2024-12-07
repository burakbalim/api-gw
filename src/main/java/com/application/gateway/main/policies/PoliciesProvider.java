package com.application.gateway.main.policies;

import com.application.gateway.main.policies.model.PoliciesCollections;
import com.application.gateway.orchestration.common.dto.ConfigurationSourceDTO;

/**
 * Interface for providing and applying policies like rate limiter for custom paths.
 *
 */
public interface PoliciesProvider {

    /**
     * Initializes the policies provider with the provided configuration source.
     *
     * @param configurationSourceDTO The configuration source to initialize with.
     */
    void init(ConfigurationSourceDTO<PoliciesCollections> configurationSourceDTO);

    /**
     * Applies the policies specified by the given name.
     *
     * @param policiesName The name of the policies to apply.
     */
    void apply(String policiesName);

}
