package com.application.gateway.orchestration.oauth2;

import com.application.gateway.main.common.RequestInfoBase;
import com.application.gateway.main.common.SessionDTO;
import com.application.gateway.main.keymanager.KeyService;
import com.application.gateway.main.keymanager.dto.KeyRequestDTO;
import com.application.gateway.orchestration.oauth2.provider.Oauth2ConfigProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class GWAuthenticationProxy {

    private final Oauth2ConfigProvider oauth2ConfigProvider;

    private final KeyService keyService;

    public void populateWithMetaData(RequestInfoBase requestInfoBase) {
        SessionDTO sessionDTO = requestInfoBase.getSessionDTO();
        if (Objects.nonNull(requestInfoBase.getSessionDTO().getClientId())) {
            sessionDTO.setMetaData(oauth2ConfigProvider.getMetaData(requestInfoBase.getSessionDTO().getClientId()));
        } else if (Objects.nonNull(requestInfoBase.getHeader("Authorization"))) {
            String authorization = requestInfoBase.getHeaders().get("Authorization").get(0);
            KeyRequestDTO keyRequestDTO = keyService.getFromKey(authorization);
            sessionDTO.setMetaData(keyRequestDTO.getMetaData());
        }
    }
}
