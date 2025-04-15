package com.application.gateway.orchestration.oauth2.config.matchers;

import com.application.gateway.orchestration.oauth2.model.ClientType;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Abstract base class for implementing custom request matchers.
 */
@Setter
@Getter
@Component
public abstract class RequestMatcherBase implements RequestMatcher {

    /**
     * List of paths to match against.
     */
    private List<String> paths;

    /**
     * The client type for which this matcher is applicable.
     */
    private ClientType type;

    /**
     * The role associated with the matched request.
     */
    private String role;

    /**
     * Gets the client type for which this matcher is applicable.
     *
     * @return The client type.
     */
    public abstract ClientType getType();

    /**
     * Matches the incoming HTTP request against configured paths.
     *
     * @param request The HTTP request to match against.
     * @return true if the request matches any configured path, otherwise false.
     */
    @Override
    public boolean matches(HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        if (Objects.isNull(paths) || paths.isEmpty()) {
            return false;
        }
        for (String path : paths) {
            Pattern templatePattern = Pattern.compile(path, Pattern.DOTALL);
            if (templatePattern.matcher(requestURI).find()) {
                return true;
            }
        }
        return false;
    }
}
