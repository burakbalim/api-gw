package com.application.gateway.orchestration.oauth2.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.application.gateway.common.util.DynamicWrapper;
import com.application.gateway.common.util.InnerData;
import com.application.gateway.orchestration.oauth2.model.User;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2ClientAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * Perform deserializer operation for OAuth2ClientAuthenticationToken.class from DB table.
 */
public class OAuth2ClientAuthenticationTokenDeserializer extends JsonDeserializer<OAuth2ClientAuthenticationToken> {

    public static final String ESCAPE = "^\"|\"$";

    @Override
    public OAuth2ClientAuthenticationToken deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        ObjectMapper mapper = (ObjectMapper) parser.getCodec();
        JsonNode root = mapper.readTree(parser);
        return deserialize(mapper, root);
    }

    private OAuth2ClientAuthenticationToken deserialize(ObjectMapper mapper, JsonNode root) throws JsonProcessingException {
        JsonNode registeredClientJsonNode = root.get("registeredClient");
        User details = mapper.treeToValue(root.get("details"), User.class);
        String clientIdIssueAtValue = getValue(registeredClientJsonNode.get("clientIdIssueAt"));
        Instant clientIdIssueAt = Objects.nonNull(clientIdIssueAtValue) ? Instant.parse(clientIdIssueAtValue) : null;
        String clientSecretExpiresAtValue = getValue(registeredClientJsonNode.get("clientSecretExpiresAt"));
        Instant clientSecretExpiresAt = Objects.nonNull(clientSecretExpiresAtValue) ? Instant.parse(clientSecretExpiresAtValue) : null;
        List<InnerData> authorizationGrantTypes = getInnerData((ArrayNode) registeredClientJsonNode.get("authorizationGrantTypes"), new ObjectMapper());

        // @formatter:off
        RegisteredClient.Builder builder = RegisteredClient.withId(getValue(registeredClientJsonNode.get("id")))
                .clientId(getValue(registeredClientJsonNode.get("clientId")))
                .clientIdIssuedAt(clientIdIssueAt)
                .clientSecret(getValue(registeredClientJsonNode.get("client_secret")))
                .clientSecretExpiresAt(clientSecretExpiresAt)
                .clientName(getValue(registeredClientJsonNode.get("client_name")))
                .scopes((set) -> {
                    set.addAll(getCollectionValue(registeredClientJsonNode.get("scopes")));
                })
                .authorizationGrantTypes((grantTypes) ->
                        authorizationGrantTypes.forEach(grantType -> grantTypes.add(resolveAuthorizationGrantType(grantType.getValue()))));
                /*.clientAuthenticationMethods((authenticationMethods) ->
                        clientAuthenticationMethods.forEach(authenticationMethod ->
                                authenticationMethods.add(resolveClientAuthenticationMethod(authenticationMethod))))*/
                /*.redirectUris((uris) -> uris.addAll(redirectUris))
                .postLogoutRedirectUris((uris) -> uris.addAll(postLogoutRedirectUris))
                .scopes((scopes) -> scopes.addAll(clientScopes));*/
        // @formatter:on

        ClientAuthenticationMethod clientAuthenticationMethod = new ClientAuthenticationMethod(root.get("clientAuthenticationMethod").get("value").asText());
        OAuth2ClientAuthenticationToken oAuth2ClientAuthenticationToken = new OAuth2ClientAuthenticationToken(builder.build(), clientAuthenticationMethod, null);
        oAuth2ClientAuthenticationToken.setDetails(details);
        return oAuth2ClientAuthenticationToken;
    }

    private Collection<String> getCollectionValue(JsonNode values) {
        List<String> scopes = new ArrayList<>();
        if (values != null && values.isArray()) {
            for (JsonNode node : values) {
                if (node.isArray()) {
                    for (JsonNode scope : node) {
                        scopes.add(scope.asText());
                    }
                }
            }
        }
        return scopes;
    }

    private String getValue(Object object) {
        if (object == null) {
            return null;
        }
        else if (object instanceof NullNode) {
            return null;
        }
        else if (object instanceof TextNode textNode) {
            return textNode.textValue().replaceAll(ESCAPE, "");
        }
        return String.valueOf(object);
    }

    private List<InnerData> getInnerData(ArrayNode arrayNode, ObjectMapper objectMapper) throws JsonProcessingException {
        String className = arrayNode.get(0).asText();
        List<InnerData> innerDataList = new ArrayList<>();
        ArrayNode innerArrayNode = (ArrayNode) arrayNode.get(1);
        for (JsonNode node : innerArrayNode) {
            InnerData innerData = objectMapper.treeToValue(node, InnerData.class);
            innerDataList.add(innerData);
        }
        DynamicWrapper dynamicWrapper = new DynamicWrapper(className, innerDataList);
        return dynamicWrapper.getInnerDataList();
    }

    private static AuthorizationGrantType resolveAuthorizationGrantType(String authorizationGrantType) {
        if (AuthorizationGrantType.AUTHORIZATION_CODE.getValue().equals(authorizationGrantType)) {
            return AuthorizationGrantType.AUTHORIZATION_CODE;
        } else if (AuthorizationGrantType.CLIENT_CREDENTIALS.getValue().equals(authorizationGrantType)) {
            return AuthorizationGrantType.CLIENT_CREDENTIALS;
        } else if (AuthorizationGrantType.REFRESH_TOKEN.getValue().equals(authorizationGrantType)) {
            return AuthorizationGrantType.REFRESH_TOKEN;
        }
        return new AuthorizationGrantType(authorizationGrantType);        // Custom authorization grant type
    }
}
