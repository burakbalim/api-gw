package com.application.gateway;

import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = GatewayApplication.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public abstract class BaseTest {
}
