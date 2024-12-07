package com.application.gateway.main.keymanager;

import com.application.gateway.common.exception.BasicTokenUnauthorizedException;
import com.application.gateway.main.common.RequestInfoBase;
import com.application.gateway.main.keymanager.dto.KeyRequestDTO;
import com.application.gateway.main.keymanager.dto.KeyResponseDTO;

import java.util.UUID;

/**
 * This interface defines methods for performing Basic Authentication using cache or databases.
 */
public interface KeyService {

    /**
     * Creates a Key by username and password using base64 encoder.
     * Does not apply time-to-live (TTL) if expire second is 0.
     *
     * @param username       The username.
     * @param keyRequestDTO  The KeyRequestDTO containing password information.
     * @return               The CreatedKeyResponse representing a unique key {UUID}.
     */
    CreatedKeyResponse addKey(String username, KeyRequestDTO keyRequestDTO);

    /**
     * Checks API ID permission with request information base.
     *
     * @param apiId            The API ID.
     * @param requestInfoBase  The RequestInfoBase containing request information.
     * @throws BasicTokenUnauthorizedException if the operation is not permitted, the key is not found,
     *                                         or the Authorization token is not found in the header.
     */
    void checkPermission(String apiId, RequestInfoBase requestInfoBase);

    /**
     * Retrieves a KeyRequestDTO by Authorization token.
     *
     * @param authorization  The Authorization token.
     * @return               The KeyRequestDTO.
     */
    KeyRequestDTO getFromKey(String authorization);

    /**
     * Retrieves all keys.
     *
     * @return  The KeyResponseDTO containing all keys.
     */
    KeyResponseDTO getList();

    /**
     * Deletes keys permanently.
     *
     * @param keyId  The ID of the key to be deleted.
     */
    void delete(UUID keyId);
}
