package com.billybang.userservice.security;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Token {

    String accessToken;
    String refreshToken;

    public Token(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
