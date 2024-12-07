package com.application.gateway.common.exception.handler;

import com.application.gateway.common.exception.*;
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
@ConditionalOnProperty(value = "error.response", havingValue = "STANDARD")
public class GlobalExceptionHandler {

    @ResponseBody
    @ExceptionHandler(UnauthorizedException.class)
    public final ResponseEntity<StandardErrorResponse> handleAllApiExceptions(UnauthorizedException ex) {
        log.error("[UnauthorizedException] requested. Message: {}", ex.getMessage());
        return new ResponseEntity<>(new StandardErrorResponse("Unauthorized"), HttpStatus.FORBIDDEN);
    }

    @ResponseBody
    @ExceptionHandler(ClientUnauthorizedException.class)
    public final ResponseEntity<ClientUnauthorizedResponse> handleAllApiExceptions(ClientUnauthorizedException ex) {
        log.trace("[ClientUnauthorizedException] requested. Message: {}", ex.getMessage());
        return new ResponseEntity<>(ex.getClientUnauthorizedResponse(), HttpStatus.FORBIDDEN);
    }

    @ResponseBody
    @ExceptionHandler(ValidationException.class)
    public final ResponseEntity<StandardErrorResponse> handleAllApiExceptions(ValidationException ex) {
        return new ResponseEntity<>(new StandardErrorResponse(ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ResponseBody
    @ExceptionHandler(RateLimitExceedException.class)
    public final ResponseEntity<StandardErrorResponse> handleAllApiExceptions(RateLimitExceedException ex) {
        log.error("[RateLimitExceedException]", ex);
        return new ResponseEntity<>(new StandardErrorResponse("Too many request"), HttpStatus.TOO_MANY_REQUESTS);
    }

    @ResponseBody
    @ExceptionHandler(BasicTokenUnauthorizedException.class)
    public final ResponseEntity<StandardErrorResponse> handleAllApiExceptions(BasicTokenUnauthorizedException ex) {
        log.error("[BasicTokenUnauthorizedException] requested. Message: {}", ex.getMessage());
        return new ResponseEntity<>(new StandardErrorResponse("User not authorised"), HttpStatus.FORBIDDEN);
    }

    /**
     * 404 not found, the response is standard
     */
    @ResponseBody
    @ExceptionHandler(ClientTypeNotFoundException.class)
    public final ResponseEntity<Void> handleAllApiExceptions(ClientTypeNotFoundException ex) {
        log.error("[ClientTypeNotFoundException]", ex);
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    /**
     * Unexpected exception, return response entity because it is unexpected.
     */
    @ResponseBody
    @ExceptionHandler(RuntimeException.class)
    public final ResponseEntity<Void> handleAllApiExceptions(RuntimeException ex) {
        log.error("[RuntimeException]", ex);
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ResponseBody
    @ExceptionHandler(ApiGatewayException.class)
    public final ResponseEntity<String> handleAllApiExceptions(ApiGatewayException ex) {
        log.error("[ApiGatewayException]", ex);
        return new ResponseEntity<>(ex.getMessage(), ex.getHttpStatus());
    }
}
