package com.application.gateway.common.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SystemEnvUtils {

    private static final String PATTERN = "\\{\\{(.*?)}}";

    private SystemEnvUtils() {

    }

    public static List<String> extract(String input) {
        Pattern compiledPattern = Pattern.compile(PATTERN);
        Matcher matcher = compiledPattern.matcher(input);

        List<String> matches = new ArrayList<>();
        while (matcher.find()) {
            matches.add(matcher.group(1));
        }
        return matches.isEmpty() ? null : matches;
    }

    public static String parseWithParams(String input) {
        List<String> params = extract(input);
        if (params != null) {
            for (String param : params) {
                input = replaceWithEnv(input, param);
            }
        }
        return input.replaceAll("(\\{\\{|}})", "");
    }

    private static String replaceWithEnv(String input, String param) {
        String env = System.getenv(param);
        if (env != null) {
            input = input.replace(param, env);
        }
        return input;
    }
}
