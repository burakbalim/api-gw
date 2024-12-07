package com.application.gateway.orchestration.common.util;

import com.application.gateway.common.exception.handler.StandardErrorResponse;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;
import org.springframework.security.oauth2.jwt.JwtValidationException;

import java.io.IOException;
import java.util.List;

public class OAuth2ErrorCodesResponseConverter {

    private static final String DEFAULT_MESSAGE = "Key not authorised";
    private static final String KEY_EXPIRED = "Key has expired, please renew";

    private OAuth2ErrorCodesResponseConverter() {
    }

    public static void convert(HttpServletResponse response, AuthenticationException exception) throws IOException {
        String message = convertMessage(exception);
        response.getWriter().write(new StandardErrorResponse(message).toString());
        response.setStatus(403);
    }

    private static String convertMessage(AuthenticationException exception) {
        if (exception.getCause() instanceof JwtValidationException jwtValidationException) {
            List<String> errors = jwtValidationException.getErrors().stream().map(OAuth2Error::getErrorCode).toList();
            if (errors.contains(OAuth2ErrorCodes.INVALID_TOKEN)) {
                return KEY_EXPIRED;
            } else {
                return DEFAULT_MESSAGE;
            }
        } else {
            return DEFAULT_MESSAGE;
        }
    }
}
