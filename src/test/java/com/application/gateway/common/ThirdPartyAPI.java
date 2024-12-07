package com.application.gateway.common;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ThirdPartyAPI {

    @LocalServerPort
    private int port;

    private final RestTemplate restTemplate;
    private final String host = "http://localhost:%d";

    public AuthTokenResponse getToken(@RequestHeader("Authorization") String token, @RequestBody Map<String, String> map) {
        HttpHeaders headers = new HttpHeaders();
        headers.put("Authorization", List.of(token));
        String format = host + "/gw/oauth2/token?grant_type=client_credentials";
        return restTemplate.exchange(String.format(format, port), HttpMethod.POST, new HttpEntity<>(map, headers),
                AuthTokenResponse.class).getBody();
    }
}
