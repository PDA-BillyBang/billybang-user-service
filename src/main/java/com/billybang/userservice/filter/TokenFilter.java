package com.billybang.userservice.filter;

import com.billybang.userservice.api.ApiResult;
import com.billybang.userservice.api.ApiUtils;
import com.billybang.userservice.exception.ErrorCode;
import com.billybang.userservice.exception.ErrorResponse;
import com.billybang.userservice.exception.ErrorResponse.FieldError;
import com.billybang.userservice.security.AuthConstant;
import com.billybang.userservice.security.UserRoleType;
import com.billybang.userservice.security.jwt.JWTConstant;
import com.billybang.userservice.service.TokenService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.*;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class TokenFilter extends OncePerRequestFilter {


    private final TokenService tokenService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws IOException, ServletException {
        try {
            if (containsJwtToken(request)) {

                Cookie jwtCookie = getJwtCookie(request);
                Claims claims = tokenService.getClaims(jwtCookie.getValue());

                if (validateClaims(claims)) {
                    setUpUserAuthentication(claims);
                }
            } else if (Objects.nonNull(request.getHeader(AuthConstant.AUTHORIZATION)) // 디버그 모드인 경우
                    && request.getHeader(AuthConstant.AUTHORIZATION).equals(AuthConstant.DEBUG_MODE)) {

                setUpAdminAuthentication();
            } else {
                SecurityContextHolder.clearContext();
            }
            filterChain.doFilter(request, response);

        } catch (ExpiredJwtException | UnsupportedJwtException | MalformedJwtException | SignatureException e) {
            SecurityContextHolder.clearContext();
            log.error(e.getMessage(), e);

            filterChain.doFilter(request, response);
        } catch (Exception e) {
            log.error(e.getMessage(), e);

            List<FieldError> fieldErrors = FieldError.of("unknown exception", e.toString(), e.getMessage());
            ApiResult<ErrorResponse> result = ApiUtils.error(ErrorResponse.of(ErrorCode.INTERNAL_SERVER_ERROR, fieldErrors));
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.getWriter().write(objectMapper.writeValueAsString(result));
        }
    }

    private boolean validateClaims(Claims claims) {
        return tokenService.getUserCode(claims.getSubject()) == (int) claims.get("code")
                && claims.get("authorities") != null;
    }

    private void setUpUserAuthentication(Claims claims) {
        @SuppressWarnings("unchecked")
        List<String> authorities = (List<String>) claims.get("authorities");
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                claims.getSubject(),
                null,
                authorities.stream().map(SimpleGrantedAuthority::new).toList());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    private void setUpAdminAuthentication() {
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                AuthConstant.ADMIN_USER,
                null,
                AuthorityUtils.commaSeparatedStringToAuthorityList(UserRoleType.ROLE_ADMIN.name()));
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    private boolean containsJwtToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return false;
        }

        return Arrays.stream(cookies)
                .anyMatch(cookie -> cookie.getName().equals(JWTConstant.ACCESS_TOKEN_NAME));
    }

    private Cookie getJwtCookie(HttpServletRequest request) {
        return Arrays.stream(request.getCookies())
                .filter(cookie -> cookie.getName().equals(JWTConstant.ACCESS_TOKEN_NAME))
                .findFirst().orElse(null);
    }
}
