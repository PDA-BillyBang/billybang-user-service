package com.billybang.userservice.controller;

import com.billybang.userservice.api.ApiResult;
import com.billybang.userservice.api.ApiUtils;
import com.billybang.userservice.api.UserApi;
import com.billybang.userservice.exception.common.BError;
import com.billybang.userservice.exception.common.CommonException;
import com.billybang.userservice.model.dto.request.LoginRequestDto;
import com.billybang.userservice.model.dto.request.SignUpRequestDto;
import com.billybang.userservice.model.dto.request.UpdateUserRequestDto;
import com.billybang.userservice.model.dto.response.LoginResponseDto;
import com.billybang.userservice.model.dto.response.SignUpResponseDto;
import com.billybang.userservice.model.dto.response.UserResponseDto;
import com.billybang.userservice.model.entity.User;
import com.billybang.userservice.model.mapper.UserMapper;
import com.billybang.userservice.service.TokenService;
import com.billybang.userservice.service.UserService;
import com.billybang.userservice.util.CookieUtils;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

import static com.billybang.userservice.security.AuthConstant.USER_ID;
import static com.billybang.userservice.security.jwt.JWTConstant.*;

@Slf4j
@RestController
@RequiredArgsConstructor
public class UserController implements UserApi {

    private final UserService userService;
    private final TokenService tokenService;
    private final UserMapper userMapper;

    @Override
    public ResponseEntity<ApiResult<SignUpResponseDto>> signUp(SignUpRequestDto requestDto) {
        User user = userService.signUp(requestDto);
        return ResponseEntity.created(null)
                .body(ApiUtils.success(userMapper.toSignUpResponseDto(user)));
    }

    @Override
    public ResponseEntity<ApiResult<LoginResponseDto>> login(LoginRequestDto requestDto) {
        try {
            User user = userService.login(requestDto);

            String accessToken = tokenService.genAccessTokenByEmail(user.getEmail());
            String refreshToken = tokenService.genRefreshTokenByEmail(user.getEmail());
            ResponseCookie accessTokenCookie = createCookie(ACCESS_TOKEN_NAME, accessToken, ACCESS_TOKEN_MAX_AGE / 1000);
            ResponseCookie refreshTokenCookie = createCookie(REFRESH_TOKEN_NAME, refreshToken, REFRESH_TOKEN_MAX_AGE / 1000);
            ResponseCookie userIdTokenCookie = createCookie(USER_ID, String.valueOf(user.getId()), ACCESS_TOKEN_MAX_AGE / 1000);

            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE,
                            accessTokenCookie.toString(),
                            refreshTokenCookie.toString(),
                            userIdTokenCookie.toString())
                    .body(ApiUtils.success(userMapper.toLoginResponseDto(user)));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new CommonException(BError.FAIL, "login");
        }
    }

    @Override
    public ResponseEntity<?> logout() {
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<ApiResult<UserResponseDto>> getUserInfo() {
        User user = userService.getUserById(userService.getLoginUserId());
        return ResponseEntity.ok(ApiUtils.success(userMapper.toUserResponseDto(user)));
    }

    @Override
    public ResponseEntity<ApiResult<?>> updateUserInfo(UpdateUserRequestDto requestDto) {
        userService.updateUser(userService.getLoginUserId(), requestDto);
        return ResponseEntity.ok(ApiUtils.success(null));
    }

    @Override
    public ResponseEntity<?> updateAccessToken(HttpServletRequest request) {
        Cookie refreshTokenCookie = Arrays.stream(request.getCookies())
                .filter(cookie -> cookie.getName().equals(REFRESH_TOKEN_NAME))
                .findFirst().orElseThrow(() -> new CommonException(BError.NOT_EXIST, "refresh token"));
        String accessToken = tokenService.genAccessTokenByRefreshToken(refreshTokenCookie.getValue());
        ResponseCookie accessTokenCookie = CookieUtils.createCookie(ACCESS_TOKEN_NAME, accessToken, ACCESS_TOKEN_MAX_AGE / 1000);
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE,
                        accessTokenCookie.toString(),
                        refreshTokenCookie.toString())
                .body(ApiUtils.success(null));
    }

    private ResponseCookie createCookie(String cookieName, String cookieValue, long maxAge) {
        return ResponseCookie.from(cookieName, cookieValue)
                .httpOnly(true)
                .path("/")
                .maxAge(maxAge)
                .build();
    }
}
