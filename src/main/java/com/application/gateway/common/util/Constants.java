package com.application.gateway.common.util;

import java.util.List;

public interface Constants {

    String GW_AUTHORIZATION = "Gw-Authorization";

    List<String> BASIC_AUTH_PATHS = List.of("api/keys/*", "api/apis/*");

    String AUTHORIZATION = "Authorization";

    String BEARER = "Bearer ";

    String BASIC = "Basic ";

    String SERVICE_SUFFIX = "-service";

    String GW = "gw";
}
