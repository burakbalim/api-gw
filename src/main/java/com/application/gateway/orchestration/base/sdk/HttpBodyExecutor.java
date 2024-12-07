package com.application.gateway.orchestration.base.sdk;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpEntity;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class HttpBodyExecutor {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private HttpBodyExecutor() {

    }

    public static Map<String, Object> extractBody(HttpEntity<?> httpEntity) {
        try {
            byte[] bytes = (byte[]) httpEntity.getBody();
            assert bytes != null;
            String bodyString = new String(bytes, StandardCharsets.UTF_8);
            return OBJECT_MAPPER.readValue(bodyString, new TypeReference<>() {});
        } catch (IOException e) {
            throw new RuntimeException("Unexpected exception. ", e);
        }
    }

    public static Map<String, Object> extractBody(InputStream inputStream) {
        try {
            return OBJECT_MAPPER.readValue(inputStream, Map.class);
        } catch (IOException e) {
            throw new RuntimeException("Unexpected exception. ", e);
        }
    }
}
