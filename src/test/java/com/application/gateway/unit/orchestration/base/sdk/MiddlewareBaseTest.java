package com.application.gateway.unit.orchestration.base.sdk;

import com.application.gateway.main.common.RequestInfoBase;
import com.application.gateway.main.common.ResponseInfo;
import com.application.gateway.orchestration.base.sdk.MiddlewareBase;
import com.application.gateway.orchestration.logger.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpEntity;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MiddlewareBaseTest {

    @Mock
    private Logger logger;

    @Mock
    private ApplicationContext applicationContext;

    @Mock
    private RequestInfoBase requestInfoBase;

    @Mock
    private ResponseInfo responseInfo;

    private TestMiddleware middleware;

    // Test için concrete implementation
    private static class TestMiddleware extends MiddlewareBase {
        @Override
        public void applyBeforeRequest(RequestInfoBase requestInfoBase) {
            // Test implementation for before request
        }

        @Override
        public void applyAfterRequest(ResponseInfo responseInfo) {
            // Test implementation for after request
        }
    }

    @BeforeEach
    void setUp() {
        middleware = new TestMiddleware();
    }

    @Test
    void setLogger_ShouldSetLoggerCorrectly() {
        // when
        middleware.setLogger(logger);

        // then
        assertNotNull(middleware.getLogger());
    }

    @Test
    void setApplicationContext_ShouldSetContextCorrectly() {
        // when
        middleware.setApplicationContext(applicationContext);

        // then
        assertNotNull(middleware.getApplicationContext());
    }

    @Test
    void getBody_WhenValidRequest_ShouldReturnMap() throws IOException {
        // given
        String jsonBody = "{\"test\":\"value\"}";
        byte[] bodyBytes = jsonBody.getBytes(StandardCharsets.UTF_8);
        HttpEntity<byte[]> httpEntity = new HttpEntity<>(bodyBytes);
        
        when(requestInfoBase.cloneHttpEntity()).thenReturn(httpEntity);

        // when
        middleware.setApplicationContext(applicationContext);
        Map<String, Object> result = middleware.getBody(requestInfoBase);

        // then
        assertNotNull(result);
        assertEquals("value", result.get("test"));
    }

    @Test
    void getBody_WhenIOException_ShouldThrowRuntimeException() throws IOException {
        // given
        when(requestInfoBase.cloneHttpEntity()).thenThrow(new IOException("Test exception"));

        // when & then
        assertThrows(RuntimeException.class, () -> middleware.getBody(requestInfoBase));
    }

    @Test
    void getBean_ShouldReturnBeanFromContext() {
        // given
        TestService testService = new TestService();
        when(applicationContext.getBean(TestService.class)).thenReturn(testService);

        // when
        middleware.setApplicationContext(applicationContext);
        TestService result = middleware.getBean(TestService.class);

        // then
        assertNotNull(result);
        verify(applicationContext).getBean(TestService.class);
    }

    @Test
    void applyBeforeRequest_ShouldExecuteSuccessfully() {
        // given
        middleware.setLogger(logger);
        middleware.setApplicationContext(applicationContext);

        // when
        middleware.applyBeforeRequest(requestInfoBase);

        // then - no exception should be thrown
    }

    @Test
    void applyAfterRequest_ShouldExecuteSuccessfully() {
        // given
        middleware.setLogger(logger);
        middleware.setApplicationContext(applicationContext);

        // when
        middleware.applyAfterRequest(responseInfo);

        // then - no exception should be thrown
    }

    // Test için yardımcı sınıf
    private static class TestService {
    }
} 