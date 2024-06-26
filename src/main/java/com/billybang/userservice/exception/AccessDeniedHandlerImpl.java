package com.billybang.userservice.exception;

import com.billybang.userservice.api.ApiResult;
import com.billybang.userservice.api.ApiUtils;
import com.billybang.userservice.exception.ErrorResponse.FieldError;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
public class AccessDeniedHandlerImpl implements AccessDeniedHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException e) throws IOException {

        List<FieldError> fieldErrors = FieldError.of("access denied exception", e.toString(), e.getMessage());
        ApiResult<ErrorResponse> result = ApiUtils.error(ErrorResponse.of(ErrorCode.ACCESS_DENIED, fieldErrors));
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.getWriter().write(objectMapper.writeValueAsString(result));
    }
}