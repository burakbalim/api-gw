package com.application.gateway.unit.main.virtualendpoints;

import com.application.gateway.main.common.VirtualEndpointRequestInfo;
import com.application.gateway.main.common.util.ExternalClassDetector;
import com.application.gateway.main.middleware.MiddlewareProvider;
import com.application.gateway.main.virtualendpoints.dto.VirtualEndpointDTO;
import com.application.gateway.main.virtualendpoints.dto.VirtualEndpointDTOCollections;
import com.application.gateway.orchestration.base.sdk.VirtualEndpoint;
import com.application.gateway.orchestration.common.dto.ConfigurationSourceDTO;
import com.application.gateway.orchestration.common.repository.ConfigurationProvider;
import com.application.gateway.orchestration.logger.Logger;
import com.application.gateway.orchestration.oauth2.GWAuthenticationProxy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VirtualEndpointProviderImplTest {
/*
    @Mock
    private ConfigurationProvider<VirtualEndpointDTOCollections> configurationProvider;

    @Mock
    private MiddlewareProvider middlewareProvider;

    @Mock
    private ExternalClassDetector<VirtualEndpoint> externalClassDetector;

    @Mock
    private GWAuthenticationProxy gwAuthenticationProxy;

    @Mock
    private Logger logger;

    @Mock
    private Environment environment;

    @Mock
    private ConfigurationSourceDTO<VirtualEndpointDTOCollections> configurationSourceDTO;

    @InjectMocks
    private VirtualEndpointProviderImpl virtualEndpointProvider;

    @BeforeEach
    void setUp() {
        VirtualEndpointDTO virtualEndpointDTO = new VirtualEndpointDTO();
        virtualEndpointDTO.setPath("/test");
        VirtualEndpointDTOCollections virtualEndpointDTOCollections = new VirtualEndpointDTOCollections(Collections.singletonList(virtualEndpointDTO));

        when(configurationProvider.loadConfiguration(configurationSourceDTO)).thenReturn(virtualEndpointDTOCollections);
        when(externalClassDetector.getBean(anyString())).thenReturn(Optional.empty());
        when(externalClassDetector.getInstanceOfClass(anyString())).thenReturn(mock(VirtualEndpoint.class));

        virtualEndpointProvider.init(configurationSourceDTO);
    }

    @Test
    void testInit() {
        verify(configurationProvider).loadConfiguration(configurationSourceDTO);
    }

    @Test
    void testIsContains() {
        assertTrue(virtualEndpointProvider.isContains("/test"));
    }

    @Test
    void testRequest() {
        VirtualEndpointRequestInfo requestInfo = new VirtualEndpointRequestInfo();
        requestInfo.setMainPath("/test");

        ResponseEntity<Object> response = virtualEndpointProvider.request(requestInfo);

        assertTrue(response != null);
    }

    @Test
    void testOnNotifyConfigurationChange() {
        virtualEndpointProvider.onNotifyConfigurationChange();

        verify(configurationProvider, times(2)).loadConfiguration(configurationSourceDTO);
    }*/
}