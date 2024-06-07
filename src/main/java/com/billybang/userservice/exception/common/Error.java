package com.billybang.userservice.exception.common;

public interface Error {
	String getCode();

	String getMessage(String... values);
}
