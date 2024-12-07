package com.application.gateway.main.gateway;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("")
public class GatewayController {

    private final GatewayService gatewayService;

    @RequestMapping("/**")
    public ResponseEntity<Object> handleRequest(HttpServletRequest request, HttpServletResponse response) {
        return gatewayService.requestExternalService(request, response);
    }
}
