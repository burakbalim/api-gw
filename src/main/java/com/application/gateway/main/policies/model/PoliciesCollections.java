package com.application.gateway.main.policies.model;

import com.application.gateway.orchestration.ConfigurationBaseDTO;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Collection;

@NoArgsConstructor
public class PoliciesCollections extends ArrayList<Policies> implements ConfigurationBaseDTO {

    public PoliciesCollections(Collection<? extends Policies> c) {
        super(c);
    }
}
