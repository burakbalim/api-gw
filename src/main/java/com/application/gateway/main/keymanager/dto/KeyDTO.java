package com.application.gateway.main.keymanager.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class KeyDTO implements Serializable {

    private String username;

    private KeyRequestDTO keyRequestDTO;
}
