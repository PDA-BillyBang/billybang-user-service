package com.billybang.userservice.security;

public class JWTConstant {
	public static final long SIGNUP_TOKEN_EXPIRED = 1000 * 60 * 60;
	public static final long ACCESS_TOKEN_EXPIRED = 1000 * 60 * 60;
	public static final long REFRESH_TOKEN_EXPIRED = 1000 * 60 * 60 * 24;
}
