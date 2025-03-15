package com.application.gateway.main.gateway;

import com.application.gateway.main.common.RequestInfoBase;
import com.application.gateway.main.common.ResponseInfo;
import com.application.gateway.main.common.util.EndpointDetector;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;


@Service
@RequiredArgsConstructor
public class GatewayServiceImpl implements GatewayService {

    private final GatewayProxy gatewayProxy;

    private final EndpointDetector endpointDetector;

    private final RequestServiceProvider requestServiceProvider;

    @Override
    public ResponseEntity<Object> requestExternalService(HttpServletRequest request, HttpServletResponse response) throws HttpClientErrorException {
        RequestInfoBase requestInfoBase = endpointDetector.detectRequestInfo(request);

        gatewayProxy.beforeRequest(requestInfoBase);

        ResponseInfo responseInfo = requestServiceProvider.request(requestInfoBase);

        gatewayProxy.afterRequest(responseInfo);

        return responseInfo.getResponse();
    }
}
