package com.application.gateway.orchestration.common.dto;

import lombok.*;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ConfigurationSourceDTO<T> implements Serializable {

    private String name;

    private String configurationSource;

    private Class<T> sourceClazz;

    public ConfigurationSourceDTO(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConfigurationSourceDTO<?> that = (ConfigurationSourceDTO<?>) o;
        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
