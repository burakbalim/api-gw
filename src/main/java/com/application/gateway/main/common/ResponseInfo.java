package com.application.gateway.main.common;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.*;

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
        this.headers = new HttpHeaders();
        setUpHeaders(responseEntity);
        this.status = responseEntity.getStatusCode();
        this.response = buildNativeHeaders(responseEntity);
        this.uri = requestInfoBase.getUri();
        this.httpMethod = requestInfoBase.getHttpMethod();
        this.mediaType = requestInfoBase.getMediaType();
        this.mainPath = requestInfoBase.getMainPath();
        this.requestHeaders = new HttpHeaders(requestInfoBase.getHeaders());
    }

    private void setUpHeaders(ResponseEntity<Object> responseEntity) {
        responseEntity.getHeaders().forEach((key, value) -> {
            if (!key.equalsIgnoreCase("Content-Length") && !key.equalsIgnoreCase("Transfer-Encoding")) {
                this.headers.put(key, value);
            }
        });
    }

    private ResponseEntity<Object> buildNativeHeaders(ResponseEntity<Object> responseEntity) {
        return ResponseEntity
                .status(responseEntity.getStatusCode())
                .headers(new HttpHeaders(this.headers))
                .contentType(MediaType.APPLICATION_JSON)
                .body(responseEntity.getBody());
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
