package com.application.gateway.orchestration.common.util;

import com.application.gateway.common.ErrorResponse;
import com.application.gateway.common.exception.UnauthorizedException;
import com.application.gateway.common.util.ObjectUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;
import org.springframework.security.oauth2.jwt.JwtValidationException;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
public class OAuth2ErrorCodesResponseConverter {

    private static final String DEFAULT_MESSAGE = "Key not authorised";
    private static final String KEY_EXPIRED = "Key has expired, please renew";

    private OAuth2ErrorCodesResponseConverter() {
    }

    public static void convert(HttpServletResponse response, Exception exception) {
        String message = convertMessage(exception);
        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(HttpStatus.UNAUTHORIZED.value())
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();

        log.error("OAuth2 Error: {} | Exception: {}", message, exception.getMessage(), exception);

        response.setContentType("application/json");
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        ObjectUtils.writeValue(response, errorResponse);
    }

    private static String convertMessage(Exception exception) {
        if (exception.getCause() instanceof JwtValidationException jwtValidationException) {
            List<String> errors = jwtValidationException.getErrors()
                    .stream()
                    .map(OAuth2Error::getErrorCode)
                    .toList();

            return errors.contains(OAuth2ErrorCodes.INVALID_TOKEN) ? KEY_EXPIRED : DEFAULT_MESSAGE;
        }
        return DEFAULT_MESSAGE;
    }
}

