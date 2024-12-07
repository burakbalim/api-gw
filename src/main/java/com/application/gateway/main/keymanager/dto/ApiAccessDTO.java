package com.application.gateway.main.keymanager.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
public class ApiAccessDTO implements Serializable {

    @JsonProperty("api_name")
    private String apiName;

    @JsonProperty("api_id")
    private String apiId;

    @JsonProperty("versions")
    private List<String> versions;

    public ApiAccessDTO(String apiId) {
        this.apiId = apiId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ApiAccessDTO)) return false;
        ApiAccessDTO that = (ApiAccessDTO) o;
        return getApiId().equals(that.getApiId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getApiId());
    }
}
