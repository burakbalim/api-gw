package com.application.gateway.common.exception.handler;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
    public class StandardErrorResponse {

    @JsonProperty("error")
    private String error;

    @Override
    public String toString() {
        return "{\"error\": \"" + error + "\"}";
    }
}
