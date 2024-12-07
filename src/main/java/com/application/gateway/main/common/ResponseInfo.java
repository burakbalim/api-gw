package com.application.gateway.main.common;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

@Getter
@Setter
public class ResponseInfo extends HttpInfo {

    private HttpHeaders headers;
    private HttpHeaders requestHeaders;
    private HttpStatusCode status;
    private ResponseEntity<Object> response;
    private String uri;
    private HttpMethod httpMethod;

    public ResponseInfo(ResponseEntity<Object> responseEntity, RequestInfoBase requestInfoBase) {
        this.headers = new HttpHeaders(responseEntity.getHeaders());
        this.status = responseEntity.getStatusCode();
        this.response = responseEntity;
        this.uri = requestInfoBase.getUri();
        this.httpMethod = requestInfoBase.getHttpMethod();
        this.mediaType = requestInfoBase.getMediaType();
        this.mainPath = requestInfoBase.getMainPath();
        this.requestHeaders = new HttpHeaders(requestInfoBase.getHeaders());
    }

    public ResponseInfo(ResponseEntity<Object> responseEntity) {
        this.headers = new HttpHeaders(responseEntity.getHeaders());
        this.status = responseEntity.getStatusCode();
        this.response = responseEntity;
    }

    public void changeBody(ResponseEntity<Object> responseEntity) {
        this.response = responseEntity;
    }
}
