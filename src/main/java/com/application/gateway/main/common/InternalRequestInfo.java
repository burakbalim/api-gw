package com.application.gateway.main.common;

import com.application.gateway.orchestration.oauth2.model.ClientType;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class InternalRequestInfo extends RequestInfoBase {

    private String serviceName;

    public InternalRequestInfo(HttpServletRequest request, ClientType clientType) {
        super(request, clientType);
    }

    public InternalRequestInfo(HttpServletRequest request, ClientType clientType, String serviceName) {
        super(request, clientType);
        this.serviceName = serviceName;
    }

    @Override
    public void addUri(String uri) {
        this.serviceName =  uri.split("/")[1];
        this.uri = uri;
    }
}
