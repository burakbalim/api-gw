package com.application.gateway.orchestration.oauth2.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * Mixin class for configuring JSON serialization and deserialization behavior for OAuth2ClientAuthenticationToken class.
 * This class applies JSON type information, custom deserializer, and visibility settings for fields.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
@JsonDeserialize(using = OAuth2ClientAuthenticationTokenDeserializer.class)
@JsonAutoDetect(
        fieldVisibility = JsonAutoDetect.Visibility.ANY,
        getterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class OAuth2ClientAuthenticationTokenMixin {
}
