package com.application.gateway.orchestration.oauth2;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public abstract class UserDTOMixin {

    @JsonCreator
    UserDTOMixin(@JsonProperty("username") String username,
                 @JsonProperty("email") String email,
                 @JsonProperty("password") String password,
                 @JsonProperty("authProvider") String authProvider,
                 @JsonProperty("externalId") String externalId,
                 @JsonProperty("birthDate") LocalDate birthDate
    ) {

    }
}
