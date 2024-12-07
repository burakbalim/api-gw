package com.application.gateway.orchestration.oauth2.registeredclient;

import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;

import java.util.HashMap;
import java.util.Map;

public class RegisteredClientRepositoryImpl implements RegisteredClientRepository {

    private final Map<String, RegisteredClient> IdRegistrationMap = new HashMap<>();

    private final Map<String, RegisteredClient> clientIdRegistrationMap = new HashMap<>();

    @Override
    public synchronized void save(RegisteredClient registeredClient) {
        if (IdRegistrationMap.get(registeredClient.getId()) != null) {
            IdRegistrationMap.remove(registeredClient.getId());
        }
        if (clientIdRegistrationMap.get(registeredClient.getClientId()) != null) {
            clientIdRegistrationMap.remove(registeredClient.getClientId());
        }
        IdRegistrationMap.put(registeredClient.getId(), registeredClient);
        clientIdRegistrationMap.put(registeredClient.getClientId(), registeredClient);
    }

    @Override
    public RegisteredClient findById(String id) {
        return IdRegistrationMap.get(id);
    }

    @Override
    public RegisteredClient findByClientId(String clientId) {
        return clientIdRegistrationMap.get(clientId);
    }
}
