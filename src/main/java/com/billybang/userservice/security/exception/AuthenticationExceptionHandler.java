package com.billybang.userservice.security.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class AuthenticationExceptionHandler implements AuthenticationEntryPoint {
	@Override
	public void commence(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
						 AuthenticationException e) throws IOException {
		httpServletResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED);
		// TODO : jwt 세부적인 예외 처리
		// httpServletResponse.sendRedirect("/exception/jwt");
	}
}
