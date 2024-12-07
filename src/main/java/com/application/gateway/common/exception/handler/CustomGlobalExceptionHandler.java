package com.application.gateway.common.exception.handler;

import com.application.gateway.common.exception.*;
import com.application.gateway.common.exception.model.ApiResponse;
import com.application.gateway.common.exception.model.ApiResult;
import com.application.gateway.orchestration.oauth2.service.ClientUnauthorizedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(value = "error.response", havingValue = "CUSTOM")
public class CustomGlobalExceptionHandler {

    @ResponseBody
    @ExceptionHandler(ApiGatewayException.class)
    public final ApiResponse<Void> handleAllApiExceptions(ApiGatewayException ex) {
        log.error(ex.getMessage());
        return new ApiResponse<>(new ApiResult(ex.getHttpStatus().value(), ex.getMessage()));
    }

    @ResponseBody
    @ExceptionHandler(UnauthorizedException.class)
    public final ApiResponse<Void> handleAllApiExceptions(UnauthorizedException ex) {
        log.error(ex.getMessage());
        return new ApiResponse<>(new ApiResult(HttpStatus.FORBIDDEN.value(), "Unauthorized"));
    }

    @ResponseBody
    @ExceptionHandler(ClientUnauthorizedException.class)
    public final ResponseEntity<ClientUnauthorizedResponse> handleAllApiExceptions(ClientUnauthorizedException ex) {
        log.trace("[ClientUnauthorizedException] requested. Message: {}", ex.getMessage());
        return new ResponseEntity<>(new ClientUnauthorizedResponse(), HttpStatus.FORBIDDEN);
    }

    @ResponseBody
    @ExceptionHandler(ValidationException.class)
    public final ApiResponse<Void> handleAllApiExceptions(ValidationException ex) {
        return new ApiResponse<>(new ApiResult(HttpStatus.BAD_REQUEST.value(), ex.getMessage()));
    }

    @ResponseBody
    @ExceptionHandler(RateLimitExceedException.class)
    public final ApiResponse<Void> handleAllApiExceptions(RateLimitExceedException ex) {
        log.error(ex.getMessage());
        return new ApiResponse<>(new ApiResult(HttpStatus.TOO_MANY_REQUESTS.value(), "Too many request"));
    }

    @ResponseBody
    @ExceptionHandler(ClientTypeNotFoundException.class)
    public final ResponseEntity<Void> handleAllApiExceptions(ClientTypeNotFoundException ex) {
        log.error(ex.getMessage());
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @ResponseBody
    @ExceptionHandler(RuntimeException.class)
    public final ResponseEntity<Void> handleAllApiExceptions(RuntimeException ex) {
        log.error(ex.getMessage());
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
