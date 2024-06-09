package com.billybang.userservice.controller;

import com.billybang.userservice.api.UserApi;
import com.billybang.userservice.exception.common.BError;
import com.billybang.userservice.exception.common.CommonException;
import com.billybang.userservice.model.dto.request.LoginRequestDto;
import com.billybang.userservice.model.dto.request.SignUpRequestDto;
import com.billybang.userservice.model.dto.response.TokenResponseDto;
import com.billybang.userservice.model.dto.response.UserResponseDto;
import com.billybang.userservice.model.entity.User;
import com.billybang.userservice.model.mapper.UserMapper;
import com.billybang.userservice.service.TokenService;
import com.billybang.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import static com.billybang.userservice.security.JWTConstant.*;

@Slf4j
@RestController
@RequiredArgsConstructor
public class UserController implements UserApi {

    private final UserService userService;
    private final TokenService tokenService;
    private final UserMapper userMapper;

    @Override
    public ResponseEntity<UserResponseDto> signUp(SignUpRequestDto requestDto) {
        User user = userService.signUp(requestDto);
        return ResponseEntity.created(null)
                .body(userMapper.toDto(user));
    }

    @Override
    public ResponseEntity login(LoginRequestDto requestDto) {
        try {
            User user = userService.login(requestDto);

            String accessToken = tokenService.genAccessTokenByEmail(user.getEmail());
            String refreshToken = tokenService.genRefreshTokenByEmail(user.getEmail());
            ResponseCookie accessTokenCookie = createCookie(accessToken, ACCESS_TOKEN, ACCESS_TOKEN_MAX_AGE);
            ResponseCookie refreshTokenCookie = createCookie(refreshToken, REFRESH_TOKEN, REFRESH_TOKEN_MAX_AGE);

            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, accessTokenCookie.toString(), refreshTokenCookie.toString())
                    .build();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new CommonException(BError.FAIL, "Login");
        }
    }

    @Override
    public ResponseEntity<String> test() {
        return null;
    }

    private ResponseCookie createCookie(String token, String cookieName, long maxAge) {
        return ResponseCookie.from(cookieName, token)
                .httpOnly(true)
                .path("/")
                .maxAge(maxAge)
                .build();
    }
}
