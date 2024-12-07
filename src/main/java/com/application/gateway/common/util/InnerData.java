package com.application.gateway.common.util;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InnerData {

    private String value;

    @Override
    public String toString() {
        return "InnerData{" +
                "value='" + value + '\'' +
                '}';
    }
}
