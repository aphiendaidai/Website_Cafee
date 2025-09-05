package com.webdemo.backend;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.webdemo.backend.Servicee.UserService;
import com.webdemo.backend.model.AuthProvider;
import com.webdemo.backend.model.User;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    @Autowired
    private UserService userService;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest oAuth2UserRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(oAuth2UserRequest);

        try {
            return processOAuth2User(oAuth2UserRequest, oAuth2User);
        } catch (Exception ex) {
            throw new InternalAuthenticationServiceException(ex.getMessage(), ex.getCause());
        }
    }

    private OAuth2User processOAuth2User(OAuth2UserRequest oAuth2UserRequest, OAuth2User oAuth2User) {
        try {
            GoogleOAuth2UserInfo oAuth2UserInfo = new GoogleOAuth2UserInfo(oAuth2User.getAttributes());
            
            if(!StringUtils.hasText(oAuth2UserInfo.getEmail())) {
                throw new OAuth2AuthenticationException("Email not found from OAuth2 provider");
            }

            Optional<User> userOptional = userService.findByEmail(oAuth2UserInfo.getEmail());
            User user;
            
            if(userOptional.isPresent()) {
                user = userOptional.get();
                if(!user.getProvider().equals(AuthProvider.GOOGLE)) {
                    throw new OAuth2AuthenticationException("Looks like you're signed up with " +
                            user.getProvider() + " account. Please use your " + user.getProvider() +
                            " account to login.");
                }
                user = updateExistingUser(user, oAuth2UserInfo);
            } else {
                user = registerNewUser(oAuth2UserRequest, oAuth2UserInfo);
            }

            return UserPrincipal.create(user, oAuth2User.getAttributes());
        } catch (Exception e) {
            System.err.println("Error processing OAuth2 user: " + e.getMessage());
            e.printStackTrace();
            throw new OAuth2AuthenticationException("Error processing OAuth2 user: " + e.getMessage());
        }
    }

    private User registerNewUser(OAuth2UserRequest oAuth2UserRequest, GoogleOAuth2UserInfo oAuth2UserInfo) {
        return userService.registerGoogleUser(
            oAuth2UserInfo.getEmail(),
            oAuth2UserInfo.getName(),
            oAuth2UserInfo.getPicture(),
            oAuth2UserInfo.getId()
        );
    }

    private User updateExistingUser(User existingUser, GoogleOAuth2UserInfo oAuth2UserInfo) {
        existingUser.setName(oAuth2UserInfo.getName());
        existingUser.setPicture(oAuth2UserInfo.getPicture());
        return userService.save(existingUser);
    }
}