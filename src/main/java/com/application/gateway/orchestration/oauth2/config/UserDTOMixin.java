package com.application.gateway.orchestration.oauth2.config;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class UserDTOMixin {

    @JsonCreator
    UserDTOMixin(@JsonProperty("username") String name) {
    }
}
