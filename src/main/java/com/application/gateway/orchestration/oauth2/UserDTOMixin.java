package com.application.gateway.orchestration.oauth2;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class UserDTOMixin {

    @JsonCreator
    UserDTOMixin(@JsonProperty("id") Long id, @JsonProperty("name") String name, @JsonProperty("surname") String surname,
                 @JsonProperty("status") String status,
                 @JsonProperty("gender") String gender,
                 @JsonProperty("phone") String phone,
                 @JsonProperty("language") String language,
                 @JsonProperty("type") String type
                 ) {

    }
}
