package com.application.gateway.orchestration.oauth2.model;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

@ReadingConverter
public class ClientTypeConverter implements Converter<String, ClientType> {

    @Override
    public ClientType convert(String source) {
        return ClientType.valueOf(source);
    }
}
