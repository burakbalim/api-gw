package com.application.gateway.common.exception;

public class UnauthorizedException extends RuntimeException {

    public UnauthorizedException() {
    }

    public UnauthorizedException(String message) {
        super(message);
    }

    public UnauthorizedException(String message, Exception e) {
         super(message, e);
    }
}
