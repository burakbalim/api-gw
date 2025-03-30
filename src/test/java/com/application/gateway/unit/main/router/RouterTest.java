package com.application.gateway.unit.main.router;

import com.application.gateway.common.util.SystemEnvUtils;
import com.application.gateway.main.router.dto.RouterBaseDTO;
import com.application.gateway.main.router.dto.RouterDTO;
import com.application.gateway.orchestration.common.dto.ConfigurationSourceDTO;
import com.application.gateway.orchestration.common.repository.ConfigurationProvider;
import com.application.gateway.orchestration.oauth2.model.ClientType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RouterTest {
/*
    @Mock
    private ConfigurationProvider<RouterDTO> configurationProvider;

    @Mock
    private ConfigurationSourceDTO<RouterDTO> configurationSourceDTO;

    @InjectMocks
    private Router router;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGet() {
        RouterBaseDTO routerBaseDTO = new RouterBaseDTO();
        routerBaseDTO.setServiceName("testService");
        routerBaseDTO.setServiceUrl("http://example.com");
        router.setMap(Collections.singletonMap("testService", routerBaseDTO));

        String serviceUrl = router.get("testService");
        assertEquals("http://example.com", serviceUrl);
    }

    @Test
    void testContains() {
        RouterBaseDTO routerBaseDTO = new RouterBaseDTO();
        routerBaseDTO.setServiceName("testService");
        routerBaseDTO.setAccessClient(Collections.singleton(ClientType.PUBLIC));
        router.setMap(Collections.singletonMap("testService", routerBaseDTO));

        assertTrue(router.contains(ClientType.PUBLIC, "testService"));
        assertFalse(router.contains(ClientType.CONFIDENTIAL, "testService"));
    }

    @Test
    void testOnNotifyConfigurationChange() {
        RouterDTO routerDTO = mock(RouterDTO.class);
        RouterBaseDTO routerBaseDTO = new RouterBaseDTO();
        routerBaseDTO.setServiceName("testService");
        routerBaseDTO.setServiceUrl("http://example.com");
        when(routerDTO.getServices()).thenReturn(Collections.singletonList(routerBaseDTO));
        when(configurationSourceDTO.getConfiguration()).thenReturn(routerDTO);

        router.init(configurationSourceDTO);
        router.onNotifyConfigurationChange();

        Map<String, RouterBaseDTO> map = router.getMap();
        assertNotNull(map);
        assertEquals(1, map.size());
        assertEquals("http://example.com", map.get("testService").getServiceUrl());
    }*/
}
