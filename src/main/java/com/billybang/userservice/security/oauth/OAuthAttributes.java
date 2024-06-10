package com.billybang.userservice.security.oauth;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;

public enum OAuthAttributes {

    KAKAO("kakao", (attributes) -> {
        // kakao는 kakao_account에 유저정보가 있다. (email)
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        // kakao_account안에 또 profile이라는 JSON객체가 있다. (nickname, profile_image)
        Map<String, Object> kakaoProfile = (Map<String, Object>)kakaoAccount.get("profile");

        OAuthUserProfile userProfile = new OAuthUserProfile();
        userProfile.setEmail((String) kakaoAccount.get("email"));
        userProfile.setNickname((String) kakaoProfile.get("nickname"));
        return userProfile;
    });

    private final String registrationId;
    private final Function<Map<String, Object>, OAuthUserProfile> of;

    OAuthAttributes(String registrationId, Function<Map<String, Object>, OAuthUserProfile> of) {
        this.registrationId = registrationId;
        this.of = of;
    }

    public static OAuthUserProfile extract(String registrationId, Map<String, Object> attributes) {
        return Arrays.stream(values())
                .filter(provider -> registrationId.equals(provider.registrationId))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new)
                .of.apply(attributes);
    }
}
