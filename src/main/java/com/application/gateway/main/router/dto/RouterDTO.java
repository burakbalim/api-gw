package com.application.gateway.main.router.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.application.gateway.orchestration.ConfigurationBaseDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RouterDTO implements ConfigurationBaseDTO {

    @JsonProperty("services")
    @Field("services")
    private List<RouterBaseDTO> services;

    @Override
    public Boolean getEnable() {
        return Boolean.TRUE;
    }
}
