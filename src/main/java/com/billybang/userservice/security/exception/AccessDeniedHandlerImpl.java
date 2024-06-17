package com.billybang.userservice.security.exception;

import com.billybang.userservice.api.ApiUtils;
import com.billybang.userservice.exception.ErrorCode;
import com.billybang.userservice.exception.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class AccessDeniedHandlerImpl implements AccessDeniedHandler {

	@Override
	public void handle(HttpServletRequest request,
					   HttpServletResponse response,
					   AccessDeniedException accessDeniedException) throws IOException {
		response.setStatus(HttpStatus.FORBIDDEN.value());
		response.getWriter().write(ResponseEntity.status(HttpStatus.FORBIDDEN.value())
						.body(ApiUtils.error(ErrorResponse.of(ErrorCode.ACCESS_DENIED)))
						.toString());
	}
}