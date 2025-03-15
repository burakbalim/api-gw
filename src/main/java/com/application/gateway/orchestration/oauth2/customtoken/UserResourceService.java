package com.application.gateway.orchestration.oauth2.customtoken;

import com.application.gateway.orchestration.oauth2.model.User;

public interface UserResourceService {

    User createOrGet(User user);

    Boolean validatePassword(String username, String password);
}
