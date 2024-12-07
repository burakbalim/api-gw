package com.application.gateway.orchestration.oauth2.model;


public enum ClientType {

    THIRD_PARTY("third_party"), PORTAL("portal"), WEB_PAGE("WEB_PAGE"), ALLOWED_PATHS("default");

    private String role;

    ClientType(String role) {
        this.role = role;
    }

    public String getRole() {
        return role;
    }
}
