package com.application.gateway.main.common;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.io.InputStream;

@Getter
@Setter
public abstract class HttpInfo {

    protected HttpMethod httpMethod;

    protected MediaType mediaType;

    protected HttpEntity<?> httpEntity;

    protected MultiValueMap<String, String> headers;

    protected String uri;

    protected String mainPath;

    public boolean isOctetStream() {
        return mediaType != null && mediaType.equals(MediaType.APPLICATION_OCTET_STREAM);
    }

    /**
     * @return Return cloned http entity after exchange internal state without add any manupulation of this state
     */
    public HttpEntity<byte[]> cloneHttpEntity() throws IOException {
        if (this.httpEntity.getBody() instanceof byte[] source) {
            byte[] target = new byte[source.length];
            System.arraycopy(source, 0, target, 0, source.length);
            return new HttpEntity<>(target, this.headers);
        } else {
            InputStream inputStream = (InputStream) this.httpEntity.getBody();
            byte[] bytes = StreamUtils.copyToByteArray(inputStream);
            assert inputStream != null;
            inputStream.close();
            this.httpEntity = new HttpEntity<>(bytes, this.headers);
            return new HttpEntity<>(bytes, this.headers);
        }
    }
}
