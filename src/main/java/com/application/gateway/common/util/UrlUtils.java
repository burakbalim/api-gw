package com.application.gateway.common.util;

public class UrlUtils {

    private UrlUtils() {

    }

    public static String getOriginalURL(String url) {
        if (url.contains(Constants.GW)) {
            return url.split(Constants.GW)[1];
        }
        return url;
    }
}
