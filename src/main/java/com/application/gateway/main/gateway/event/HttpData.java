package com.application.gateway.main.gateway.event;

import com.application.gateway.common.HttpInputType;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.MultiValueMap;

import java.util.Map;

@Getter
@Setter
public class HttpData {

    private HttpInputType type;

    private Map<String, Object> body;

    private MultiValueMap<String, String> headers;

    private String uri;

    private String httpMethod;
}
