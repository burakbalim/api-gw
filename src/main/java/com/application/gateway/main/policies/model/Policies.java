package com.application.gateway.main.policies.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.application.gateway.orchestration.ConfigurationBaseDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Policies implements ConfigurationBaseDTO {

    @JsonProperty("name")
    @Field("name")
    private String name;

    @JsonProperty("state")
    @Field("state")
    private PolicyState state;

    @JsonProperty("per")
    @Field("per")
    private Integer per;

    @JsonProperty("rate")
    @Field("rate")
    private Integer rate;

    @JsonProperty("quota_max")
    @Field("quota_max")
    private Integer quotaMax;

    @JsonProperty("quota_renewal_rate")
    @Field("quota_renewal_rate")
    private Integer quotaRenewalRate;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Policies)) return false;
        Policies policies = (Policies) o;
        return getName().equals(policies.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName());
    }
}
