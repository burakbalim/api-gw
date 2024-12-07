package com.application.gateway.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ApiGatewayException extends RuntimeException {

    private static final String LOG_FORMAT = "{'http_status': '%s', 'service_name': '%s', 'path': '%s', 'messages', '%s'}";

    private final HttpStatus httpStatus;

    public ApiGatewayException(HttpStatus httpStatus, String serviceName, String path) {
        super(String.format(LOG_FORMAT, httpStatus, serviceName, path, ""));
        this.httpStatus = httpStatus;
    }

    public ApiGatewayException(HttpStatus httpStatus, String serviceName, String path, String message) {
        super(String.format(LOG_FORMAT, httpStatus, serviceName, path, message));
        this.httpStatus = httpStatus;
    }

    public ApiGatewayException(HttpStatus httpStatus) {
        super(String.format(LOG_FORMAT, httpStatus, "", "", ""));
        this.httpStatus = httpStatus;
    }

}
