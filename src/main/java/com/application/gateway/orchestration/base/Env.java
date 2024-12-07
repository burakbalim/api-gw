package com.application.gateway.orchestration.base;

import lombok.Getter;
import org.springframework.core.env.Environment;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Getter
public enum Env {

    PROD("prod"), TEST("test"), STAGING("staging"), DEV("dev");

    private final String envName;

    Env(String envName) {
        this.envName = envName;
    }

    private static Map<String, Env> ENV_MAP;

    static {
        ENV_MAP = Arrays.stream(Env.values()).collect(Collectors.toMap(Env::getEnvName, item -> item));
    }

    public static Env from(Environment environment) {
        List<Env> collect = Arrays.stream(environment.getActiveProfiles()).filter(item -> Objects.nonNull(ENV_MAP.get(item))).map(item -> ENV_MAP.get(item)).toList();
        return collect.iterator().next();
    }
}
