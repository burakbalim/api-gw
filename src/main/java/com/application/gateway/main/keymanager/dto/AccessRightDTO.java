package com.application.gateway.main.keymanager.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
public class AccessRightDTO implements Serializable {

    @JsonProperty("access_apis")
    List<ApiAccessDTO> accessApiList;
}
