package com.application.gateway.unit.orchestration.base.sdk;

import com.application.gateway.orchestration.base.sdk.HttpBodyExecutor;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class HttpBodyExecutorTest {

    @Test
    void extractBody_WhenValidHttpEntity_ShouldReturnMap() {
        // given
        String jsonBody = "{\"key\":\"value\", \"number\":42}";
        byte[] bodyBytes = jsonBody.getBytes(StandardCharsets.UTF_8);
        HttpEntity<byte[]> httpEntity = new HttpEntity<>(bodyBytes);

        // when
        Map<String, Object> result = HttpBodyExecutor.extractBody(httpEntity);

        // then
        assertNotNull(result);
        assertEquals("value", result.get("key"));
        assertEquals(42, result.get("number"));
    }

    @Test
    void extractBody_WhenValidInputStream_ShouldReturnMap() {
        // given
        String jsonBody = "{\"name\":\"test\", \"active\":true}";
        InputStream inputStream = new ByteArrayInputStream(jsonBody.getBytes(StandardCharsets.UTF_8));

        // when
        Map<String, Object> result = HttpBodyExecutor.extractBody(inputStream);

        // then
        assertNotNull(result);
        assertEquals("test", result.get("name"));
        assertEquals(true, result.get("active"));
    }

    @Test
    void extractBody_WhenInvalidJsonHttpEntity_ShouldThrowException() {
        // given
        String invalidJson = "{invalid-json}";
        byte[] bodyBytes = invalidJson.getBytes(StandardCharsets.UTF_8);
        HttpEntity<byte[]> httpEntity = new HttpEntity<>(bodyBytes);

        // when & then
        assertThrows(RuntimeException.class, () -> HttpBodyExecutor.extractBody(httpEntity));
    }

    @Test
    void extractBody_WhenInvalidJsonInputStream_ShouldThrowException() {
        // given
        String invalidJson = "{invalid-json}";
        InputStream inputStream = new ByteArrayInputStream(invalidJson.getBytes(StandardCharsets.UTF_8));

        // when & then
        assertThrows(RuntimeException.class, () -> HttpBodyExecutor.extractBody(inputStream));
    }

    @Test
    void extractBody_WhenEmptyHttpEntity_ShouldThrowException() {
        // given
        HttpEntity<byte[]> httpEntity = new HttpEntity<>(new byte[0]);

        // when & then
        assertThrows(RuntimeException.class, () -> HttpBodyExecutor.extractBody(httpEntity));
    }

    @Test
    void extractBody_WhenComplexJsonHttpEntity_ShouldReturnNestedMap() {
        // given
        String jsonBody = "{\"user\":{\"name\":\"John\",\"age\":30},\"settings\":{\"enabled\":true}}";
        byte[] bodyBytes = jsonBody.getBytes(StandardCharsets.UTF_8);
        HttpEntity<byte[]> httpEntity = new HttpEntity<>(bodyBytes);

        // when
        Map<String, Object> result = HttpBodyExecutor.extractBody(httpEntity);

        // then
        assertNotNull(result);
        assertTrue(result.get("user") instanceof Map);
        assertTrue(result.get("settings") instanceof Map);
        
        @SuppressWarnings("unchecked")
        Map<String, Object> user = (Map<String, Object>) result.get("user");
        assertEquals("John", user.get("name"));
        assertEquals(30, user.get("age"));
    }
} 