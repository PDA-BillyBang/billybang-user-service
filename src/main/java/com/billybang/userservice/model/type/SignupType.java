package com.billybang.userservice.model.type;

public enum SignupType {

    KAKAO("카카오"),
    GENERAL("일반");

    private final String value;

    SignupType(String value) {
        this.value = value;
    }
}
