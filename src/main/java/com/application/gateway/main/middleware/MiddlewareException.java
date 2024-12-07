package com.application.gateway.main.middleware;

public class MiddlewareException extends RuntimeException {

    public MiddlewareException(String message) {
        super(message);
    }
}
