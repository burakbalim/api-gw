package com.application.gateway.common.exception;

import java.text.MessageFormat;

public class RateLimitExceedException extends RuntimeException {

    private static final String MESSAGE = "Rate limit exceed for policy key: {0}";

    public RateLimitExceedException(String key) {
        super(MessageFormat.format(MESSAGE, key));
    }
}
