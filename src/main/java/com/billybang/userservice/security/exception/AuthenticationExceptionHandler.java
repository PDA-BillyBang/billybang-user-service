package com.billybang.userservice.security.exception;

import com.billybang.userservice.api.ApiResult;
import com.billybang.userservice.api.ApiUtils;
import com.billybang.userservice.exception.ErrorCode;
import com.billybang.userservice.exception.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class AuthenticationExceptionHandler implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException e) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        ApiResult<ErrorResponse> result = ApiUtils.error(ErrorResponse.of(ErrorCode.UNAUTHORIZED));

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.getWriter().write(objectMapper.writeValueAsString(result));
    }
}
