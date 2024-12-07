package com.application.gateway.common.exception;

public class BasicTokenUnauthorizedException extends UnauthorizedException {

    public BasicTokenUnauthorizedException(String message) {
        super(message);
    }
}
