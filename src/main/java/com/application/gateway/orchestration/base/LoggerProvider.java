package com.application.gateway.orchestration.base;

import java.util.logging.Level;

/**
 * Interface for providing logging functionality.
 */
public interface LoggerProvider {

    /**
     * Logs the provided text at the specified log level.
     *
     * @param level The log level at which to log the text.
     * @param text  The text to log.
     */
    void log(Level level, String text);

}
