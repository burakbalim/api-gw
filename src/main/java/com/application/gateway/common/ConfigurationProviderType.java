package com.application.gateway.common;

import java.util.function.Supplier;

public enum ConfigurationProviderType {

    FILE,
    MONGO,
    POSTGRES;

    private static final String GW = "gw_";
    private static final String JSON = ".json";

    public static String getFileName(ConfigurationProviderType type, Supplier<String> supplier) {
        return switch (type) {
            case FILE -> supplier.get() + JSON;
            case MONGO -> GW + supplier.get();
            case POSTGRES -> GW + supplier.get();
        };
    }
}
