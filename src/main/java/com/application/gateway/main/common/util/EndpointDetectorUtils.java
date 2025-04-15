package com.application.gateway.main.common.util;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.application.gateway.common.util.Constants.SERVICE_SUFFIX;

public class EndpointDetectorUtils {

    static final String SERVICE_SUFFIX = "-service";

    public static Optional<String> findServiceUrl(String mainPath) {
        Pattern pattern = Pattern.compile("([^/]+?-service)");
        Matcher matcher = pattern.matcher(mainPath);
        if (matcher.find()) {
            return Optional.of(matcher.group(1));
        }
        return Optional.empty();
    }
}
