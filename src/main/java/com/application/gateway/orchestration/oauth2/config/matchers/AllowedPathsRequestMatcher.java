package com.application.gateway.orchestration.oauth2.config.matchers;

import com.application.gateway.orchestration.oauth2.model.ClientType;
import org.springframework.stereotype.Component;

@Component
public class AllowedPathsRequestMatcher extends RequestMatcherBase {

    @Override
    public ClientType getType() {
        return ClientType.ALLOWED_PATHS;
    }
}
