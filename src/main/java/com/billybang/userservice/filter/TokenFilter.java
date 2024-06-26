package com.billybang.userservice.filter;

import com.billybang.userservice.exception.ErrorCode;
import com.billybang.userservice.security.AuthConstant;
import com.billybang.userservice.security.UserRoleType;
import com.billybang.userservice.security.jwt.JWTConstant;
import com.billybang.userservice.service.TokenService;
import io.jsonwebtoken.*;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static com.billybang.userservice.security.jwt.JWTConstant.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class TokenFilter extends OncePerRequestFilter {


    private final TokenService tokenService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws IOException, ServletException {
        try {
            if (containsAccessToken(request)) {

                String accessToken = getAccessTokenCookie(request).getValue();
                setUpUserAuthentication(accessToken);
            } else if (!containsAccessToken(request) && containsRefreshToken(request)) {
                Cookie refreshTokenCookie = getRefreshTokenCookie(request);
                String refreshToken = refreshTokenCookie.getValue();
                String accessToken = tokenService.genAccessTokenByRefreshToken(refreshToken);
                setUpUserAuthentication(accessToken);

                addCookies(response, accessToken, refreshToken);
            } else if (Objects.nonNull(request.getHeader(AuthConstant.AUTHORIZATION)) // 디버그 모드인 경우
                    && request.getHeader(AuthConstant.AUTHORIZATION).equals(AuthConstant.DEBUG_MODE)) {

                setUpAdminAuthentication();
            } else {
                SecurityContextHolder.clearContext();
                request.setAttribute("exception", ErrorCode.UNAUTHORIZED.getCode());
            }

        } catch (ExpiredJwtException | UnsupportedJwtException | MalformedJwtException | SignatureException e) {
            log.error(e.getMessage(), e);

            SecurityContextHolder.clearContext();
            clearCookies(response);
            setRequestAttributes(request, ErrorCode.INVALID_TOKEN, e);
        } catch (Exception e) {
            log.error(e.getMessage(), e);

            clearCookies(response);
            setRequestAttributes(request, ErrorCode.INTERNAL_SERVER_ERROR, e);
        }
        filterChain.doFilter(request, response);
    }

    private void setUpUserAuthentication(String accessToken) {
        Claims claims = tokenService.getClaims(accessToken);

        if (validateClaims(claims)) {
            @SuppressWarnings("unchecked")
            List<String> authorities = (List<String>) claims.get("authorities");
            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                    claims.getSubject(),
                    null,
                    authorities.stream().map(SimpleGrantedAuthority::new).toList());
            SecurityContextHolder.getContext().setAuthentication(auth);
        }
    }

    private boolean validateClaims(Claims claims) {
        return tokenService.getUserCode(claims.getSubject()) == (int) claims.get("code")
                && claims.get("authorities") != null;
    }

    private void setUpAdminAuthentication() {
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                AuthConstant.ADMIN_USER,
                null,
                AuthorityUtils.commaSeparatedStringToAuthorityList(UserRoleType.ROLE_ADMIN.name()));
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    private boolean containsAccessToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return false;
        }

        return Arrays.stream(cookies)
                .anyMatch(cookie -> cookie.getName().equals(JWTConstant.ACCESS_TOKEN_NAME));
    }

    private boolean containsRefreshToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return false;
        }

        return Arrays.stream(cookies)
                .anyMatch(cookie -> cookie.getName().equals(JWTConstant.REFRESH_TOKEN_NAME));
    }

    private Cookie getAccessTokenCookie(HttpServletRequest request) {
        return Arrays.stream(request.getCookies())
                .filter(cookie -> cookie.getName().equals(JWTConstant.ACCESS_TOKEN_NAME))
                .findFirst().orElse(null);
    }

    private Cookie getRefreshTokenCookie(HttpServletRequest request) {
        return Arrays.stream(request.getCookies())
                .filter(cookie -> cookie.getName().equals(JWTConstant.REFRESH_TOKEN_NAME))
                .findFirst().orElse(null);
    }

    private Cookie createCookie(String cookieName, String cookieValue, long maxAge) {
        Cookie cookie = new Cookie(cookieName, cookieValue);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge((int) maxAge);
        cookie.setDomain("www.billybang.me");
        return cookie;
    }

    private void addCookies(HttpServletResponse response, String accessToken, String refreshToken) {
        response.addCookie(createCookie(ACCESS_TOKEN_NAME, accessToken, ACCESS_TOKEN_MAX_AGE / 1000));
        response.addCookie(createCookie(REFRESH_TOKEN_NAME, refreshToken, REFRESH_TOKEN_MAX_AGE / 1000));
    }

    private void clearCookies(HttpServletResponse response) {
        response.addCookie(createCookie(ACCESS_TOKEN_NAME, "", 0));
        response.addCookie(createCookie(REFRESH_TOKEN_NAME, "", 0));
    }

    private void setRequestAttributes(HttpServletRequest request, ErrorCode invalidToken, Exception e) {
        request.setAttribute("exception", invalidToken.getCode());
        request.setAttribute("error", e.toString());
        request.setAttribute("errorMessage", e.getMessage());
    }
}
