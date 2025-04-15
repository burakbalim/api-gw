package com.application.gateway.orchestration.oauth2.provider;

import com.application.gateway.orchestration.common.dto.ConfigurationSourceDTO;
import com.application.gateway.orchestration.oauth2.model.ClientType;
import com.application.gateway.orchestration.oauth2.model.OAuth2ConfigurationList;

import java.security.Principal;
import java.util.Map;
import java.util.Optional;

/**
 * Interface for providing OAuth2 configuration details and operations.
 * Hold associated paths with client-type in in-memory
 *
 */
public interface Oauth2ConfigProvider {

    /**
     * Initializes the OAuth2 configuration provider with the provided configuration source.
     * <p>
     * Populate RegisteredClientRepository with clients
     * Hold associated paths with client-type in in-memory
     * @param configurationSourceDTO The configuration source to initialize with.
     */
    void init(ConfigurationSourceDTO<OAuth2ConfigurationList> configurationSourceDTO);

    /**
     * Retrieves metadata for the specified client ID.
     *
     * @param clientId The ID of the client for which metadata is requested.
     * @return A map containing metadata for the client, or an empty map if metadata is not available.
     */
    Map<String, String> getMetaData(String clientId);

    /**
     * Retrieves the client type based on the provided principal and main path.
     *
     * @param principal The principal representing the authenticated user.
     * @param mainPath  The main path associated with the request.
     * @return An optional containing the client type if it can be determined, or empty if not.
     */
    Optional<ClientType> getClientType(Principal principal, String mainPath);

}

