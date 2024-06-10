package com.billybang.userservice.security.oauth;

import com.billybang.userservice.exception.common.BError;
import com.billybang.userservice.exception.common.CommonException;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;

public enum OAuthAttributes {

    KAKAO("kakao", OAuthAttributes::getKakaoUserProfile);

    private final String registrationId;
    private final Function<Map<String, Object>, OAuthUserProfile> getUserProfile;

    OAuthAttributes(String registrationId, Function<Map<String, Object>, OAuthUserProfile> getUserProfile) {
        this.registrationId = registrationId;
        this.getUserProfile = getUserProfile;
    }

    public static OAuthUserProfile extract(String registrationId, Map<String, Object> attributes) {
        return Arrays.stream(values())
                .filter(provider -> registrationId.equals(provider.registrationId))
                .findFirst()
                .orElseThrow(() -> new CommonException(BError.NOT_REGISTERED, registrationId))
                .getUserProfile.apply(attributes);
    }

    private static OAuthUserProfile getKakaoUserProfile(Map<String, Object> attributes) {
        // kakao 는 "kakao_account" 에 유저정보가 있다. (email, profile 등)
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        // "kakao_account" 안에 또 "profile" 이라는 JSON 객체가 있다. (nickname 등)
        Map<String, Object> kakaoProfile = (Map<String, Object>) kakaoAccount.get("profile");

        return OAuthUserProfile.builder()
                .email(String.valueOf(kakaoAccount.get("email")))
                .nickname(String.valueOf(kakaoProfile.get("nickname")))
                .provider("kakao")
                .build();
    }
}
