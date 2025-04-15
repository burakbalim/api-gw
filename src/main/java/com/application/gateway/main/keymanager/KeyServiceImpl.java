package com.application.gateway.main.keymanager;

import com.application.gateway.common.cache.Cache;
import com.application.gateway.common.exception.BasicTokenUnauthorizedException;
import com.application.gateway.main.common.RequestInfoBase;
import com.application.gateway.main.keymanager.dto.ApiAccessDTO;
import com.application.gateway.main.keymanager.dto.KeyDTO;
import com.application.gateway.main.keymanager.dto.KeyRequestDTO;
import com.application.gateway.main.keymanager.dto.KeyResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class KeyServiceImpl implements KeyService {

    private static final String GW_CACHE_AUTH_CACHE_NAME = "GW::BASIC_AUTH::";

    private static final String GW_CACHE_KEY_ID_NAME = "GW::KEYID::";

    private static final String BASIC = "Basic ";

    private final Cache<String, String> userNameToKeyCache;

    private final Cache<String, KeyDTO> keyIDToKeyRequest;

    @Override
    public CreatedKeyResponse addKey(String username, KeyRequestDTO keyRequestDTO) {
        log.info("Adding key for username: {} with expire_timestamp: {}", username, keyRequestDTO.getExpireSecs());
        String password = keyRequestDTO.getBasicAuthDataDTO().getPassword();
        String basicToken = prepareBasicToken(username, password);
        Long expireSecs = keyRequestDTO.getExpireSecs();
        UUID keyID = UUID.randomUUID();
        String firstKey = GW_CACHE_AUTH_CACHE_NAME + basicToken;
        removeKeyIfExist(firstKey);
        if (expireSecs != 0) {
            long ttlSeconds = expireSecs - System.currentTimeMillis() / 1000;
            userNameToKeyCache.put(firstKey, keyID.toString(), ttlSeconds);
            keyIDToKeyRequest.put(GW_CACHE_KEY_ID_NAME + keyID, new KeyDTO(username, keyRequestDTO), ttlSeconds);
        } else {
            userNameToKeyCache.put(firstKey, keyID.toString());
            keyIDToKeyRequest.put(GW_CACHE_KEY_ID_NAME + keyID, new KeyDTO(username, keyRequestDTO));
        }

        return new CreatedKeyResponse(keyID.toString());
    }

    @Override
    public void checkPermission(String apiId, RequestInfoBase requestInfoBase) {
        List<String> useBasicAuths = requestInfoBase.getHeader("Authorization");
        if (Objects.isNull(useBasicAuths) || useBasicAuths.isEmpty()) {
            throw new BasicTokenUnauthorizedException("Auth token not found");
        }
        KeyRequestDTO keyRequestDTO = getKeyDTO(useBasicAuths.get(0));
        if (Objects.isNull(keyRequestDTO)) {
            throw new BasicTokenUnauthorizedException("Key not available");
        }
        if (!keyRequestDTO.getAccessRightDTO().getAccessApiList().contains(new ApiAccessDTO(apiId))) {
            throw new BasicTokenUnauthorizedException("Api Id not accessed");
        }
    }

    @Override
    public KeyRequestDTO getFromKey(String authorization) {
        KeyRequestDTO keyRequestDTO = getKeyDTO(authorization);
        if (Objects.isNull(keyRequestDTO)) {
            throw new BasicTokenUnauthorizedException("Key not available");
        }
        return keyRequestDTO;
    }

    @Override
    public KeyResponseDTO getList() {
        Set<String> keys = keyIDToKeyRequest.getAllKeys(GW_CACHE_KEY_ID_NAME + "*");
        KeyResponseDTO keyResponseDTO = new KeyResponseDTO();
        keyResponseDTO.setKeys(keys.stream().map(key -> key.substring(GW_CACHE_KEY_ID_NAME.length())).collect(Collectors.toSet()));
        return keyResponseDTO;
    }

    @Override
    public void delete(UUID keyId) {
        KeyDTO keyDTO = keyIDToKeyRequest.get(GW_CACHE_KEY_ID_NAME + keyId);
        userNameToKeyCache.remove(GW_CACHE_AUTH_CACHE_NAME + prepareBasicToken(keyDTO.getUsername(), keyDTO.getKeyRequestDTO().getBasicAuthDataDTO().getPassword()));
        keyIDToKeyRequest.remove(GW_CACHE_KEY_ID_NAME + keyId);
    }

    private void removeKeyIfExist(String firstKey) {
        if (Objects.nonNull(keyIDToKeyRequest.get(firstKey))){
            String keyId = userNameToKeyCache.get(firstKey);
            delete(UUID.fromString(keyId));
        }
    }

    private KeyRequestDTO getKeyDTO(String authorization) {
        String keyId = userNameToKeyCache.get(GW_CACHE_AUTH_CACHE_NAME + authorization);
        if (Objects.isNull(keyId)) {
            throw new BasicTokenUnauthorizedException("KeyId not available on first cache");
        }
        KeyDTO keyDTO = keyIDToKeyRequest.get(GW_CACHE_KEY_ID_NAME + keyId);
        if (Objects.isNull(keyDTO)) {
            throw new BasicTokenUnauthorizedException("KeyDTO not available on second cache");
        }
        return keyDTO.getKeyRequestDTO();
    }

    private static String prepareBasicToken(String username, String password) {
        return BASIC + Base64.getEncoder().encodeToString((username + ":" + password).getBytes());
    }
}
