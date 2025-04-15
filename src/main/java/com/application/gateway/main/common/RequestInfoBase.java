package com.application.gateway.main.common;

import com.application.gateway.common.util.StreamUtils;
import com.application.gateway.orchestration.oauth2.model.ClientType;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.ToString;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.security.Principal;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Getter
@ToString
public abstract class RequestInfoBase extends HttpInfo {

    private final String host;

    private final SessionDTO sessionDTO;

    RequestInfoBase (HttpServletRequest request, ClientType clientType) {
        this.httpMethod = HttpMethod.valueOf(request.getMethod());
        this.httpEntity = getHttpEntity(request);
        this.headers = getHeaders(request);
        this.sessionDTO = getSessionDTO(request, clientType);
        this.host = request.getServerName();
        this.uri = getAbsoluteUrl(request);
        this.mainPath = request.getRequestURI();
        this.mediaType = findMediaPart(request);
    }

    private MediaType findMediaPart(HttpServletRequest request) {
        String contentType = request.getHeader("Content-Type");
        if (contentType != null) {
            try {
                return MediaType.parseMediaType(contentType);
            } catch (IllegalArgumentException e) {
                return null;
            }
        }
        return null;
    }

    private String getAbsoluteUrl(HttpServletRequest request) {
        if (Objects.nonNull(request.getQueryString()) && !request.getQueryString().isEmpty()) {
            return request.getRequestURI() + "?" + request.getQueryString();
        }
        return request.getRequestURI();
    }

    private static HttpEntity<Object> getHttpEntity(HttpServletRequest request) {
        try {
            ServletInputStream inputStream = request.getInputStream();
            /*byte[] bytes = StreamUtils.copyToByteArray(inputStream);
            assert inputStream != null;
            inputStream.close();*/
            return new HttpEntity<>(StreamUtils.copyToByteArray(inputStream), getHeaders(request));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static HttpHeaders getHeaders(HttpServletRequest request) {
        HttpHeaders headers = new HttpHeaders();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            Enumeration<String> headerValues = request.getHeaders(headerName);
            while (headerValues.hasMoreElements()) {
                String headerValue = headerValues.nextElement();
                headers.add(headerName, headerValue);
            }
        }
        return headers;
    }

    public void removeHeader(String headerName) {
        headers.remove(headerName);
    }

    public void removeHeaders(List<String> headerNames) {
        headerNames.forEach(this::removeHeader);
    }

    public void addHeader(String headerName, Object headerValue) {
        headers.add(headerName, String.valueOf(headerValue));
    }

    public void addHeaders(Map<String, Object> headerMap) {
        headerMap.forEach(this::addHeader);
    }

    public List<String> getHeader(String headerName) {
        return headers.get(headerName);
    }

    private SessionDTO getSessionDTO(HttpServletRequest request, ClientType clientType) {
        Principal userPrincipal = request.getUserPrincipal();
        return Objects.nonNull(userPrincipal) ? new SessionDTO(userPrincipal.getName(), clientType) : new SessionDTO(clientType);
    }

    public void addUri(String uri) {
        this.uri = uri;
    }

    public void changeMediaType(MediaType mediaType) {
        this.mediaType = mediaType;
    }

    public void rearrangeHttpEntity() {
        try {
            this.httpEntity = this.cloneHttpEntity();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
