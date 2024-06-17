package com.billybang.userservice.service;

import com.billybang.userservice.model.entity.User;
import com.billybang.userservice.security.jwt.JWTConstant;
import com.billybang.userservice.security.UserRoleType;
import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class TokenService {

    // 서버가 구동되면 SECRET_KEY 는 매번 초기화한다
    private final String SECRET_KEY = UUID.randomUUID().toString();
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
        Claims claims = validateToken(refreshToken);
        String username = claims.getSubject();
        return genAccessTokenByEmail(username);
    }

    public Claims validateToken(String jwtToken) {
        try {
            return Jwts.parser().setSigningKey(SECRET_KEY.getBytes()).parseClaimsJws(jwtToken).getBody();
        } catch (ExpiredJwtException | UnsupportedJwtException | MalformedJwtException | SignatureException |
                 IllegalArgumentException exception) {
            log.error(exception.getMessage());
            throw new JwtException("invalid jwt token");
        }
    }
}

