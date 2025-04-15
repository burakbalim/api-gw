package com.application.gateway.main.custompaths.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PathDTO implements Serializable {

    @JsonProperty("method")
    private String method;

    @JsonProperty("endpoint")
    private String endpoint;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PathDTO)) return false;
        PathDTO pathDTO = (PathDTO) o;
        return Objects.equals(getMethod(), pathDTO.getMethod()) && Objects.equals(getEndpoint(), pathDTO.getEndpoint());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getMethod(), getEndpoint());
    }
}
