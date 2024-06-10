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
        OAuth2User oAuth2User = delegate.loadUser(userRequest); // OAuth 서비스(kakao, google, naver)에서 가져온 유저 정보를 담고있음

        String registrationId = userRequest.getClientRegistration().getRegistrationId(); // OAuth 서비스 이름(ex. kakao, naver, google)
        String userNameAttributeName = userRequest.getClientRegistration()
                .getProviderDetails()
                .getUserInfoEndpoint()
                .getUserNameAttributeName(); // OAuth 로그인 시 키(pk)가 되는 값
        Map<String, Object> attributes = oAuth2User.getAttributes(); // OAuth 서비스의 유저 정보들

        OAuthUserProfile userProfile = OAuthAttributes.extract(registrationId, attributes); // registrationId에 따라 유저 정보를 통해 공통된 UserProfile 객체로 만들어 줌
        User user = saveOrUpdate(userProfile);

        Map<String, Object> customAttribute = customAttribute(attributes, userNameAttributeName, userProfile);

        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority(UserRoleType.ROLE_CUSTOMER.name())),
                customAttribute,
                userNameAttributeName);

    }

    private Map<String, Object> customAttribute(Map<String, Object> attributes, String userNameAttributeName, OAuthUserProfile userProfile) {
        Map<String, Object> customAttribute = new LinkedHashMap<>();
        customAttribute.put(userNameAttributeName, attributes.get(userNameAttributeName));
        customAttribute.put("email", userProfile.getEmail());
        customAttribute.put("nickname", userProfile.getNickname());
        return customAttribute;

    }

    private User saveOrUpdate(OAuthUserProfile userProfile) {

        User user = userRepository.findByEmail(userProfile.getEmail())
                .map(u -> u.update(userProfile.getEmail(), userProfile.getNickname())) // OAuth 서비스 사이트에서 유저 정보 변경이 있을 수 있기 때문에 우리 DB에도 update
                .orElse(userProfile.toUser());

        return userRepository.save(user);
    }

}
