package com.application.gateway.orchestration.oauth2.config.mixin;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public abstract class UserDTOMixin {

    @JsonCreator
    UserDTOMixin(@JsonProperty("id") Long id,
                 @JsonProperty("username") String username,
                 @JsonProperty("email") String email,
                 @JsonProperty("auth_provider") String authProvider,
                 @JsonProperty("external_id") String externalId,
                 @JsonProperty("birth_date") LocalDate birthDate
    ) {

    }
}
