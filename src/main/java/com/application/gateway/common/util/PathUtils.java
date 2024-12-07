package com.application.gateway.common.util;

import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

public class PathUtils {

    public static final String LAST_CHAR = "/";

    private PathUtils() {
    }

    public static boolean isPathMatch(List<String> paths, String mainPath) {
        if (Objects.isNull(paths)) {
            return false;
        }
        for (String path : paths) {
            Pattern templatePattern = Pattern.compile(path, Pattern.DOTALL);
            if (templatePattern.matcher(mainPath).find()) {
                return true;
            }
        }
        return false;
    }

    public static String convertPathFromSlash(String path) {
        if (path.endsWith(LAST_CHAR)) {
            return path.substring(0, path.length() - 1);
        }
        return path;
    }
}
