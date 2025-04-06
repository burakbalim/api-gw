package com.application.gateway.orchestration.oauth2.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("username")
    private String username;

    @JsonProperty("email")
    private String email;

    @JsonProperty("auth_provider")
    private String authProvider; // "GOOGLE", "APPLE", etc.

    @JsonProperty("external_id")
    private String externalId; // External provider user ID

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @JsonProperty("birth_date")
    private String birthDate;

    @JsonProperty("password")
    private String password;

    public User(String username, String authProvider) {
        this.username = username;
        this.authProvider = authProvider;
    }

}
