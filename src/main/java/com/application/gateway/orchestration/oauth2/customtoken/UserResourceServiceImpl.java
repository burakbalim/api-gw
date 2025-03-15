package com.application.gateway.orchestration.oauth2.customtoken;

import com.application.gateway.orchestration.oauth2.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class UserResourceServiceImpl implements UserResourceService {

    @Value("${oauth.user.resource.create}")
    private String resourceCreate;

    @Value("${oauth.user.resource.validate}")
    private String resourceValidate;

    private final RestTemplate restTemplate;

    @Override
    public User createOrGet(User user) {
        ResponseEntity<User> response = restTemplate.postForEntity(resourceCreate, user, User.class);
        if (response.getStatusCode().is2xxSuccessful()) {
            return response.getBody();
        }
        throw new RuntimeException("Failed to create or get user: " + response.getStatusCode());
    }

    @Override
    public Boolean validatePassword(String username, String password) {
        ResponseEntity<Boolean> response = restTemplate.getForEntity(String.format(resourceValidate, username, password), Boolean.class);
        return response.getBody();
    }
}
