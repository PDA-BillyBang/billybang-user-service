package com.billybang.userservice.service;

import com.billybang.userservice.model.entity.User;
import com.billybang.userservice.security.jwt.JWTConstant;
import com.billybang.userservice.security.UserRoleType;
import io.jsonwebtoken.*;
import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class TokenService {

    @Value("${jwt.secret-key}")
    private String SECRET_KEY;
    // User 에 대한 변경사항이 있을 경우 해당 코드가 변경되고 이를 기반으로 기존 유저의 수정이 발생했을때 토큰을 무효화한다
    private final Map<String, Integer> USER_CODE = new HashMap<>();
    private final UserService userService;

    private void setUserCode(String username, LocalDateTime updateTime) {
        USER_CODE.put(username, updateTime.hashCode());
    }

    public int getUserCode(String username) {
        return USER_CODE.get(username);
    }

    public String genAccessTokenByEmail(String email) {
        User user = userService.getUserByEmail(email);
        setUserCode(email, user.getUpdatedAt());
        List<GrantedAuthority> grantedAuthorities = AuthorityUtils
                .commaSeparatedStringToAuthorityList(UserRoleType.ROLE_CUSTOMER.name());

        return Jwts
                .builder()
                .setSubject(email)
                .setIssuer("BILLYBANG_ADMIN")
                .claim("authorities", grantedAuthorities.stream().map(GrantedAuthority::getAuthority).toList())
                .claim("code", USER_CODE.get(email))
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + JWTConstant.ACCESS_TOKEN_MAX_AGE))
                .signWith(SignatureAlgorithm.HS512, SECRET_KEY.getBytes()).compact();
    }

    public String genRefreshTokenByEmail(String email) {
        return Jwts
                .builder()
                .setSubject(email)
                .setIssuer("BILLYBANG_ADMIN")
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + JWTConstant.REFRESH_TOKEN_MAX_AGE))
                .signWith(SignatureAlgorithm.HS512, SECRET_KEY.getBytes()).compact();
    }

    public String genAccessTokenByRefreshToken(String refreshToken) {
        Claims claims = getClaims(refreshToken);
        String username = claims.getSubject();
        return genAccessTokenByEmail(username);
    }

    public Claims getClaims(String jwtToken) {
        return Jwts.parser().setSigningKey(SECRET_KEY.getBytes()).parseClaimsJws(jwtToken).getBody();
    }

    public Boolean validateRequestContextToken() {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        Cookie[] cookies = Objects.requireNonNull(requestAttributes).getRequest().getCookies();
        if (cookies == null) return false;

        Cookie accessTokenCookie = Arrays.stream(cookies)
                .filter(cookie -> cookie.getName().equals(JWTConstant.ACCESS_TOKEN_NAME))
                .findFirst().orElse(null);
        if (accessTokenCookie == null) return false;

        try {
            getClaims(accessTokenCookie.getValue());
        } catch (Exception e) {
            log.error(e.getMessage());
            return false;
        }
        return true;
    }
}