package com.application.gateway.common.exception;

public class ValidationException extends RuntimeException{

    public ValidationException(String message) {
        super(message);
    }
}
