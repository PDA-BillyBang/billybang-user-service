package com.billybang.userservice.util;

import org.springframework.http.ResponseCookie;

public class CookieUtils {

    public static ResponseCookie createCookie(String cookieName, String cookieValue, long maxAge) {
        return ResponseCookie.from(cookieName, cookieValue)
                .httpOnly(true)
                .path("/")
                .maxAge(maxAge)
                .build();
    }
}
