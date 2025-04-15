package com.application.gateway.common.util;

import java.util.Base64;

public class Decoder {

    public static final String SPLINTER = ":";

    private Decoder() {
    }

    public static String[] decodeAuthorizationHeader(String authorizationHeader) {
        String base64Credentials = authorizationHeader.substring("Basic ".length()).trim();
        String credentials = new String(Base64.getDecoder().decode(base64Credentials));
        return credentials.split(SPLINTER);
    }
}
