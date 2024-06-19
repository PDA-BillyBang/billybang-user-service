package com.billybang.userservice.security.jwt;

public class JWTConstant {
	public static final String ACCESS_TOKEN_NAME = "access_token";
	public static final String REFRESH_TOKEN_NAME = "refresh_token";
	public static final long ACCESS_TOKEN_MAX_AGE = 1000 * 60 * 60;
	public static final long REFRESH_TOKEN_MAX_AGE = 1000 * 60 * 60 * 24;
}
