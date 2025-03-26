package com.application.gateway.common.exception.handler;

import com.application.gateway.common.ErrorResponse;
import com.application.gateway.common.exception.*;
import com.application.gateway.orchestration.oauth2.exception.ClientUnauthorizedException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDateTime;
import java.util.Objects;

@ControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class GlobalExceptionHandler {

    @ResponseBody
    @ExceptionHandler(UnauthorizedException.class)
    public final ResponseEntity<ErrorResponse> handleAllApiExceptions(HttpServletRequest request, UnauthorizedException ex) {
        return buildErrorResponse(ex, "invalid_user", HttpStatus.UNAUTHORIZED, request);
    }

    @ResponseBody
    @ExceptionHandler(ClientUnauthorizedException.class)
    public final ResponseEntity<ClientUnauthorizedResponse> handleAllApiExceptions(ClientUnauthorizedException ex) {
        log.trace("[ClientUnauthorizedException] requested. Message: {}", ex.getMessage());
        return new ResponseEntity<>(ex.getClientUnauthorizedResponse(), HttpStatus.UNAUTHORIZED);
    }

    @ResponseBody
    @ExceptionHandler(ValidationException.class)
    public final ResponseEntity<ErrorResponse> handleAllApiExceptions(HttpServletRequest request, ValidationException ex) {
        return buildErrorResponse(ex, ex.getMessage(), HttpStatus.BAD_REQUEST, request);
    }

    @ResponseBody
    @ExceptionHandler(RateLimitExceedException.class)
    public final ResponseEntity<ErrorResponse> handleAllApiExceptions(HttpServletRequest request, RateLimitExceedException ex) {
        return buildErrorResponse(ex, "too_many_request", HttpStatus.TOO_MANY_REQUESTS, request);
    }

    @ResponseBody
    @ExceptionHandler(BasicTokenUnauthorizedException.class)
    public final ResponseEntity<ErrorResponse> handleAllApiExceptions(HttpServletRequest request, BasicTokenUnauthorizedException ex) {
        return buildErrorResponse(ex, "invalid_user", HttpStatus.UNAUTHORIZED, request);
    }

    /**
     * 404 not found, the response is standard
     */
    @ResponseBody
    @ExceptionHandler(ClientTypeNotFoundException.class)
    public final ResponseEntity<ErrorResponse> handleAllApiExceptions(HttpServletRequest request, ClientTypeNotFoundException ex) {
        return buildErrorResponse(ex, "client_type", HttpStatus.NOT_FOUND, request);
    }

    /**
     * Unexpected exception, return response entity because it is unexpected.
     */
    @ResponseBody
    @ExceptionHandler(RuntimeException.class)
    public final ResponseEntity<ErrorResponse> handleAllApiExceptions(HttpServletRequest request, RuntimeException ex) {
        return buildErrorResponse(ex, "internal", HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    @ResponseBody
    @ExceptionHandler(ApiGatewayException.class)
    public final ResponseEntity<ErrorResponse> handleAllApiExceptions(HttpServletRequest request, ApiGatewayException ex) {
        return buildErrorResponse(ex, ex.getMessage(), ex.getHttpStatus(), request);
    }

    private static ResponseEntity<ErrorResponse> buildErrorResponse(Exception ex, String message, HttpStatus status, HttpServletRequest request) {
        log.error("Exception: {} | Path: {}", ex.getMessage(), request.getRequestURI(), ex);

        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(status.value())
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(status).body(errorResponse);
    }
}
