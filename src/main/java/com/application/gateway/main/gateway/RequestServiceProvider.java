package com.application.gateway.main.gateway;

import com.application.gateway.common.exception.ApiGatewayException;
import com.application.gateway.common.util.UrlUtils;
import com.application.gateway.main.common.*;
import com.application.gateway.main.custompaths.CustomPathProvider;
import com.application.gateway.main.router.Router;
import com.application.gateway.main.virtualendpoints.VirtualEndpointProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class RequestServiceProvider {

    private final VirtualEndpointRequestServiceImpl virtualEndpointRequestService;

    private final InternalEndpointRequestServiceImpl externalEndpointRequestService;

    private final CustomPathProvider customPathProvider;

    public ResponseInfo request(RequestInfoBase requestInfoBase) {
        customPathProvider.applyBeforeRequest(requestInfoBase);

        ResponseEntity<Object> responseEntity = req(requestInfoBase);

        ResponseInfo responseInfo = new ResponseInfo(responseEntity, requestInfoBase);

        customPathProvider.applyAfterRequest(responseInfo);

        return responseInfo;
    }

    private ResponseEntity<Object> req(RequestInfoBase requestInfoBase) {
        ResponseEntity<Object> responseEntity;
        if (requestInfoBase instanceof InternalRequestInfo) {
            responseEntity = externalEndpointRequestService.request(requestInfoBase);
        }
        else {
            responseEntity = virtualEndpointRequestService.request(requestInfoBase);
        }
        return responseEntity;
    }

    private interface EndpointRequestService {
        ResponseEntity<Object> request(RequestInfoBase requestInfoBase);
    }

    @Service
    @RequiredArgsConstructor
    private static class VirtualEndpointRequestServiceImpl implements EndpointRequestService{

        private final VirtualEndpointProvider virtualEndpointProvider;

        @Override
        public ResponseEntity<Object> request(RequestInfoBase requestInfoBase) {
             VirtualEndpointRequestInfo virtualRequestInfo  = (VirtualEndpointRequestInfo) requestInfoBase;

             return virtualEndpointProvider.request(virtualRequestInfo);
        }
    }

    @Service
    @RequiredArgsConstructor
    private static class InternalEndpointRequestServiceImpl implements EndpointRequestService {

        private final Router router;

        private final RestTemplate restTemplate;

        @Override
        public ResponseEntity<Object> request(RequestInfoBase requestInfoBase) {
            InternalRequestInfo internalRequestInfo = (InternalRequestInfo) requestInfoBase;

            if (!router.contains(internalRequestInfo.getSessionDTO().getClientType(), internalRequestInfo.getServiceName())) {
                throw new ApiGatewayException(HttpStatus.NOT_FOUND, internalRequestInfo.getServiceName(), internalRequestInfo.getMainPath(), "No associated service found");
            }

            String target = router.get(internalRequestInfo.getServiceName()) + UrlUtils.getOriginalURL(internalRequestInfo.getUri());

            try {
                return request(internalRequestInfo, target);
            } catch (HttpStatusCodeException e) {
                return ResponseEntity.status(e.getStatusCode()).headers(e.getResponseHeaders()).body(e.getResponseBodyAsString());
            }
        }

        private ResponseEntity<Object> request(InternalRequestInfo internalRequestInfo, String target) {
            Class<?> responseType;
            if (internalRequestInfo.isOctetStream()) {
                responseType = byte[].class;
            }
            else {
                responseType = Object.class;
            }
            ResponseEntity<?> responseEntity = restTemplate.exchange(target, internalRequestInfo.getHttpMethod(), internalRequestInfo.getHttpEntity(), responseType);
            return new ResponseEntity<>(responseEntity.getBody(), responseEntity.getHeaders(), responseEntity.getStatusCode());
        }
    }
}
