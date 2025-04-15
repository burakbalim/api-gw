package com.application.gateway.main.keymanager.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class BasicAuthDataDTO implements Serializable {

    @NotNull
    @JsonProperty("password")
    private String password;
}
