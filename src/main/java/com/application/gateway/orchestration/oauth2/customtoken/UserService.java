package com.application.gateway.orchestration.oauth2.customtoken;

import com.application.gateway.orchestration.oauth2.model.UserDTO;

public interface UserService {

    UserDTO getUserDTO(String username, String password);
}
