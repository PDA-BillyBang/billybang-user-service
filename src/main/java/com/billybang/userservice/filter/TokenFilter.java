package com.billybang.userservice.filter;

import com.billybang.userservice.security.AuthConstant;
import com.billybang.userservice.security.jwt.JWTConstant;
import com.billybang.userservice.security.UserRoleType;
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
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class TokenFilter extends OncePerRequestFilter {

    private final TokenService tokenService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            if (checkJWTToken(request)) {
                Cookie jwtCookie = Arrays.stream(request.getCookies())
                        .filter(cookie -> cookie.getName().equals(JWTConstant.ACCESS_TOKEN_NAME))
                        .findFirst()
                        .orElse(null);
                Claims claims = tokenService.validateToken(Objects.requireNonNull(jwtCookie).getValue());

                if (tokenService.getUserCode(claims.getSubject()) == (int) claims.get("code")
                        && claims.get("authorities") != null) {
                    setUpSpringAuthentication(claims);
                }
            } else if (Objects.nonNull(request.getHeader(AuthConstant.AUTHORIZATION)) // 디버그 모드인 경우
                    && request.getHeader(AuthConstant.AUTHORIZATION).equals(AuthConstant.DEBUG_MODE)) {

                SecurityContextHolder.getContext().setAuthentication(
                        new UsernamePasswordAuthenticationToken("admin", null,
                                AuthorityUtils.commaSeparatedStringToAuthorityList(UserRoleType.ROLE_ADMIN.name())));
            } else {
                SecurityContextHolder.clearContext();
            }
            filterChain.doFilter(request, response);
        } catch (ExpiredJwtException | UnsupportedJwtException | MalformedJwtException | SignatureException e) {
            SecurityContextHolder.clearContext();
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private void setUpSpringAuthentication(Claims claims) {
        @SuppressWarnings("unchecked")
        List<String> authorities = (List) claims.get("authorities");
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                claims.getSubject(),
                null,
                authorities.stream().map(SimpleGrantedAuthority::new).toList());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    private boolean checkJWTToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (Objects.isNull(cookies)) {
            return false;
        }

        Optional<Cookie> jwtCookie = Arrays.stream(cookies)
                .filter(cookie -> cookie.getName().equals(JWTConstant.ACCESS_TOKEN_NAME))
                .findFirst();
        return jwtCookie.isPresent();
    }
}
