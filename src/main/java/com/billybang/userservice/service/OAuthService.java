package com.billybang.userservice.service;

import com.billybang.userservice.model.entity.User;
import com.billybang.userservice.repository.UserRepository;
import com.billybang.userservice.security.oauth.OAuthAttributes;
import com.billybang.userservice.security.oauth.OAuthUserProfile;
import com.billybang.userservice.security.UserRoleType;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class OAuthService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest); // OAuth 서비스(kakao)에서 가져온 유저 정보를 담고 있음

        String registrationId = userRequest.getClientRegistration().getRegistrationId(); // OAuth 서비스 이름(eg. kakao)
        String userNameAttributeName = userRequest.getClientRegistration()
                .getProviderDetails()
                .getUserInfoEndpoint()
                .getUserNameAttributeName(); // OAuth 로그인 시 키(pk)가 되는 값 (kakao 의 경우 "id"이고, 회원번호에 해당함)
        Map<String, Object> attributes = oAuth2User.getAttributes(); // OAuth 서비스의 유저 정보들

        // registrationId 에 따라 유저 정보를 통해 UserProfile 객체로 만들어 줌
        OAuthUserProfile userProfile = OAuthAttributes.extract(registrationId, attributes);
        User user = saveOrUpdate(userProfile);

        Map<String, Object> customAttributes = customAttributes(attributes, userNameAttributeName, userProfile);

        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority(UserRoleType.ROLE_CUSTOMER.name())),
                customAttributes,
                userNameAttributeName);

    }

    private Map<String, Object> customAttributes(Map<String, Object> attributes,
                                                 String userNameAttributeName,
                                                 OAuthUserProfile userProfile) {
        Map<String, Object> customAttributes = new LinkedHashMap<>();
        customAttributes.put(userNameAttributeName, attributes.get(userNameAttributeName));
        customAttributes.put("email", userProfile.getEmail());
        customAttributes.put("nickname", userProfile.getNickname());
        return customAttributes;

    }

    private User saveOrUpdate(OAuthUserProfile userProfile) {

        User user = userRepository.findByEmail(userProfile.getEmail())
                .map(u -> u.update(userProfile.getEmail(), userProfile.getNickname())) // OAuth provider 에서 유저 정보 변경이 있을 수 있음
                .orElse(userProfile.toUserEntity());

        return userRepository.save(user);
    }

}
