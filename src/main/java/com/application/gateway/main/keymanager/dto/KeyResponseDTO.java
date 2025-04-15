package com.application.gateway.main.keymanager.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class KeyResponseDTO {

    private Set<String> keys;
}
