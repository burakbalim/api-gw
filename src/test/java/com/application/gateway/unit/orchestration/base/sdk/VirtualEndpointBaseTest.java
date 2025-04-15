package com.application.gateway.unit.orchestration.base.sdk;

import com.application.gateway.main.common.RequestInfoBase;
import com.application.gateway.main.common.VirtualEndpointRequestInfo;
import com.application.gateway.orchestration.base.Env;
import com.application.gateway.orchestration.base.LoggerProvider;
import com.application.gateway.orchestration.base.sdk.VirtualEndpointBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VirtualEndpointBaseTest {

    @Mock
    private LoggerProvider loggerProvider;

    @Mock
    private Env env;

    @Mock
    private RequestInfoBase requestInfoBase;

    private TestVirtualEndpoint virtualEndpoint;

    // Test için concrete implementation
    private static class TestVirtualEndpoint extends VirtualEndpointBase {
        @Override
        public ResponseEntity<Object> apply(VirtualEndpointRequestInfo requestInfo) {
            return ResponseEntity.ok().build();
        }
    }

    @BeforeEach
    void setUp() {
        virtualEndpoint = new TestVirtualEndpoint();
    }

    @Test
    void setLogger_ShouldSetLoggerCorrectly() {
        // when
        virtualEndpoint.setLogger(loggerProvider);

        // then
        assertNotNull(virtualEndpoint.getLogger());
    }

    @Test
    void setEnv_ShouldSetEnvCorrectly() {
        // when
        virtualEndpoint.setEnv(env);

        // then
        assertNotNull(virtualEndpoint.getEnv());
    }

    @Test
    void getBody_WhenValidRequest_ShouldReturnMap() throws IOException {
        // given
        String jsonBody = "{\"test\":\"value\"}";
        byte[] bodyBytes = jsonBody.getBytes(StandardCharsets.UTF_8);
        HttpEntity<byte[]> httpEntity = new HttpEntity<>(bodyBytes);
        
        when(requestInfoBase.cloneHttpEntity()).thenReturn(httpEntity);

        // when
        Map<String, Object> result = virtualEndpoint.getBody(requestInfoBase);

        // then
        assertNotNull(result);
        assertEquals("value", result.get("test"));
    }

    @Test
    void getBody_WhenIOException_ShouldThrowRuntimeException() throws IOException {
        // given
        when(requestInfoBase.cloneHttpEntity()).thenThrow(new IOException("Test exception"));

        // when & then
        assertThrows(RuntimeException.class, () -> virtualEndpoint.getBody(requestInfoBase));
    }

    @Test
    void getBody_WhenEmptyBody_ShouldThrowException() throws IOException {
        // given
        HttpEntity<byte[]> httpEntity = new HttpEntity<>(new byte[0]);
        when(requestInfoBase.cloneHttpEntity()).thenReturn(httpEntity);

        // when & then
        assertThrows(RuntimeException.class, () -> virtualEndpoint.getBody(requestInfoBase));
    }

    @Test
    void getBody_WhenComplexJson_ShouldReturnNestedMap() throws IOException {
        // given
        String jsonBody = "{\"user\":{\"name\":\"test\",\"age\":25},\"active\":true}";
        byte[] bodyBytes = jsonBody.getBytes(StandardCharsets.UTF_8);
        HttpEntity<byte[]> httpEntity = new HttpEntity<>(bodyBytes);
        
        when(requestInfoBase.cloneHttpEntity()).thenReturn(httpEntity);

        // when
        Map<String, Object> result = virtualEndpoint.getBody(requestInfoBase);

        // then
        assertNotNull(result);
        assertInstanceOf(Map.class, result.get("user"));
        assertEquals(true, result.get("active"));
        
        @SuppressWarnings("unchecked")
        Map<String, Object> user = (Map<String, Object>) result.get("user");
        assertEquals("test", user.get("name"));
        assertEquals(25, user.get("age"));
    }
} 