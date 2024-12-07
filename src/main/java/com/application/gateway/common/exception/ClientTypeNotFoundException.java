package com.application.gateway.common.exception;

public class ClientTypeNotFoundException extends RuntimeException {

    private static final String MESSAGE = "Unexpected url: %s";

    public ClientTypeNotFoundException(String url){
        super(String.format(MESSAGE, url));
    }
}
