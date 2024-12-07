package com.application.gateway.orchestration.oauth2.config.matchers;

import com.application.gateway.orchestration.oauth2.model.ClientType;
import org.springframework.stereotype.Component;

@Component
public class ThirdPartyRequestMatcher extends RequestMatcherBase{

    @Override
    public ClientType getType() {
        return ClientType.THIRD_PARTY;
    }
}
