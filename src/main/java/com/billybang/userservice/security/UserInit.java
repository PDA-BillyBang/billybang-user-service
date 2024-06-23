package com.billybang.userservice.security;

import com.billybang.userservice.model.dto.request.LoginRequestDto;
import com.billybang.userservice.model.dto.request.SignUpRequestDto;
import com.billybang.userservice.model.dto.response.TokenResponseDto;
import com.billybang.userservice.service.TokenService;
import com.billybang.userservice.service.UserService;
import io.jsonwebtoken.Claims;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.List;

@Profile({"local", "local-cloud", "test", "dev"})
@Component
@RequiredArgsConstructor
public class UserInit {

    private final UserService userService;
    private final TokenService tokenService;

    @PostConstruct
    public void init() {
        SignUpRequestDto dto = SignUpRequestDto.builder()
                .email(AuthConstant.ADMIN_USER)
                .password(AuthConstant.ADMIN_PWD)
                .nickname("admin")
                .build();

        userService.signUp(dto);
        authorize();

    }

    private void authorize() {
        LoginRequestDto loginDto = LoginRequestDto.builder()
                .email(AuthConstant.ADMIN_USER)
                .password(AuthConstant.ADMIN_PWD)
                .build();
        TokenResponseDto token = TokenResponseDto.builder()
                .accessToken(tokenService.genAccessTokenByEmail(loginDto.getEmail()))
                .refreshToken(tokenService.genRefreshTokenByEmail(loginDto.getEmail()))
                .build();

        Claims claims = tokenService.getClaims(token.getAccessToken());
        List<String> authorities = (List) claims.get("authorities");
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                claims.getSubject(), null,
                authorities.stream().map(SimpleGrantedAuthority::new).toList());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }
}
