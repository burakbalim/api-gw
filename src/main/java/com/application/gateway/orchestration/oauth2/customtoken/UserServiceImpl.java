package com.application.gateway.orchestration.oauth2.customtoken;

import com.application.gateway.orchestration.oauth2.model.UserDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserDetailsService userDetailsService;

    @Override
    public UserDTO getUserDTO(String username, String password) {
        /*try {
            User user = (User) userDetailsService.loadUserByUsername(username);

            if (Objects.isNull(user) || !user.getPassword().equals(password) || !user.getUsername().equals(username)) {
                throw new OAuth2AuthenticationException(OAuth2ErrorCodes.ACCESS_DENIED);
            }
        } catch (UsernameNotFoundException e) {
            throw new OAuth2AuthenticationException(OAuth2ErrorCodes.ACCESS_DENIED);
        }*/
        return new UserDTO(1 , username, "default", 1, "male", "dumpy-phone", "en", "dummpy-type");
    }
}
