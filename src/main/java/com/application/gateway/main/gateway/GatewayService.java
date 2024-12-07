package com.application.gateway.main.gateway;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;

public interface GatewayService {

    ResponseEntity<Object> requestExternalService(HttpServletRequest request, HttpServletResponse response) throws HttpClientErrorException;
}
