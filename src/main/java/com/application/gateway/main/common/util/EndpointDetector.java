package com.application.gateway.main.common.util;

import com.application.gateway.common.exception.ClientTypeNotFoundException;
import com.application.gateway.main.common.InternalRequestInfo;
import com.application.gateway.main.common.RequestInfoBase;
import com.application.gateway.main.common.UndefinedRequestInfo;
import com.application.gateway.main.common.VirtualEndpointRequestInfo;
import com.application.gateway.main.router.RouterService;
import com.application.gateway.main.virtualendpoints.VirtualEndpointProvider;
import com.application.gateway.orchestration.oauth2.model.ClientType;
import com.application.gateway.orchestration.oauth2.provider.Oauth2ConfigProvider;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static com.application.gateway.main.common.util.EndpointDetectorUtils.findServiceUrl;

@Component
@RequiredArgsConstructor
public class EndpointDetector {

    private final VirtualEndpointProvider virtualEndpointProvider;

    private final Oauth2ConfigProvider oauth2ConfigProvider;

    private final RouterService routerService;

    /**
     * Detect requests which belong to type(Virtual, Internal, Undefined)
     */
    public RequestInfoBase detectRequestInfo(HttpServletRequest request) {
        String uri = request.getRequestURI();
        ClientType clientType = oauth2ConfigProvider.getClientType(request.getUserPrincipal(), uri).orElseThrow(() -> new ClientTypeNotFoundException(uri));
        Optional<String> serviceUrlOptional = findServiceUrl(uri);
        if (virtualEndpointProvider.isContains(uri)) {
            return new VirtualEndpointRequestInfo(request, clientType);
        }
        else if (serviceUrlOptional.isPresent() && routerService.contains(clientType, serviceUrlOptional.get())) {
            return new InternalRequestInfo(request, clientType, serviceUrlOptional.get());
        }
        else {
            return new UndefinedRequestInfo(request, clientType);
        }
    }
}
