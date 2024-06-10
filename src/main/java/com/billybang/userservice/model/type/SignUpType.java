package com.billybang.userservice.model.type;

import com.billybang.userservice.exception.common.BError;
import com.billybang.userservice.exception.common.CommonException;

import java.util.Arrays;

public enum SignUpType {

    KAKAO("kakao"),
    BILLYBANG("billybang");

    private final String provider;

    SignUpType(String provider) {
        this.provider = provider;
    }

    public static SignUpType getSignUpType(String providerName) {
        return Arrays.stream(values())
                .filter(type -> type.provider.equals(providerName))
                .findFirst()
                .orElseThrow(() -> new CommonException(BError.NOT_EXIST, "sign up type of " + providerName));
    }
}
