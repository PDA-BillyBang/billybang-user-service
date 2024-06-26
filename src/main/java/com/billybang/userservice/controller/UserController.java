package com.billybang.userservice.controller;

import com.billybang.userservice.api.ApiResult;
import com.billybang.userservice.api.ApiUtils;
import com.billybang.userservice.api.UserApi;
import com.billybang.userservice.model.dto.request.*;
import com.billybang.userservice.model.dto.response.*;
import com.billybang.userservice.model.entity.User;
import com.billybang.userservice.model.entity.UserInfo;
import com.billybang.userservice.model.mapper.UserInfoMapper;
import com.billybang.userservice.model.mapper.UserMapper;
import com.billybang.userservice.service.TokenService;
import com.billybang.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.billybang.userservice.security.jwt.JWTConstant.*;

@Slf4j
@RestController
@RequiredArgsConstructor
public class UserController implements UserApi {

    private final UserService userService;
    private final TokenService tokenService;
    private final UserMapper userMapper;
    private final UserInfoMapper userInfoMapper;

    @Override
    public ResponseEntity<ApiResult<SignUpResponseDto>> signUp(SignUpRequestDto requestDto) {
        User user = userService.signUp(requestDto);
        return ResponseEntity.created(null)
                .body(ApiUtils.success(userMapper.toSignUpResponseDto(user)));
    }

    @Override
    public ResponseEntity<ApiResult<UserInfoResponseDto>> addUserInfo(UserInfoRequestDto requestDto) {
        UserInfo userInfo = userService.addUserInfo(requestDto);
        return ResponseEntity.created(null)
                .body(ApiUtils.success(userInfoMapper.toDto(userInfo)));
    }

    @Override
    public ResponseEntity<ApiResult<LoginResponseDto>> login(LoginRequestDto requestDto) {
        User user = userService.login(requestDto);

        String accessToken = tokenService.genAccessTokenByEmail(user.getEmail());
        String refreshToken = tokenService.genRefreshTokenByEmail(user.getEmail());
        ResponseCookie accessTokenCookie = createCookie(ACCESS_TOKEN_NAME, accessToken, ACCESS_TOKEN_MAX_AGE / 1000);
        ResponseCookie refreshTokenCookie = createCookie(REFRESH_TOKEN_NAME, refreshToken, REFRESH_TOKEN_MAX_AGE / 1000);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE,
                        accessTokenCookie.toString(),
                        refreshTokenCookie.toString())
                .body(ApiUtils.success(userMapper.toLoginResponseDto(user)));
    }

    @Override
    public ResponseEntity<?> logout() {
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<ApiResult<GetUserInfoResponseDto>> getUserInfo() {
        User user = userService.getUserById(userService.getLoginUserId());
        return ResponseEntity.ok(ApiUtils.success(userMapper.toGetUserInfoResponseDto(user)));
    }

    @Override
    public ResponseEntity<ApiResult<?>> updateUserPassword(UpdatePwdRequestDto updatePassword) {
        userService.updateUserPassword(updatePassword.getPassword());
        return ResponseEntity.ok(ApiUtils.success(null));
    }

    @Override
    public ResponseEntity<ApiResult<?>> updateUserNickname(UpdateNicknameRequestDto updateNickname) {
        userService.updateUserNickname(updateNickname.getNickname());
        return ResponseEntity.ok(ApiUtils.success(null));
    }

    @Override
    public ResponseEntity<ApiResult<UserInfoResponseDto>> updateUserInfo(UserInfoRequestDto requestDto) {
        userService.updateUserInfo(requestDto);
        UserInfo userInfo = userService.getUserInfo();
        return ResponseEntity.ok(ApiUtils.success(userInfoMapper.toDto(userInfo)));
    }

    @Override
    public ResponseEntity<ApiResult<?>> deleteUser() {
        userService.deleteUser();
        return ResponseEntity.ok(ApiUtils.success(null));
    }

    @Override
    public ResponseEntity<ApiResult<ValidateEmailResponseDto>> validateEmail(String email) {
        Boolean idDuplicate = userService.validateDuplicateEmail(email);
        return ResponseEntity.ok(ApiUtils.success(
                ValidateEmailResponseDto.builder()
                        .existsByEmail(idDuplicate)
                        .build())
        );
    }

    @Override
    public ResponseEntity<ApiResult<ValidateNicknameResponseDto>> validateNickname(String nickname) {
        Boolean isDuplicate = userService.validateDuplicateNickname(nickname);
        return ResponseEntity.ok(ApiUtils.success(
                ValidateNicknameResponseDto.builder()
                        .existsByNickname(isDuplicate)
                        .build())
        );
    }

    @Override
    public ResponseEntity<ApiResult<ValidateTokenResponseDto>> validateToken() {
        Boolean isValid = tokenService.validateRequestContextToken();
        return ResponseEntity.ok(ApiUtils.success(
                ValidateTokenResponseDto.builder()
                        .isValid(isValid)
                        .build())
        );
    }

    @GetMapping("/test")
    public void test() {
        log.info("test");
    }

    private ResponseCookie createCookie(String cookieName, String cookieValue, long maxAge) {
        return ResponseCookie.from(cookieName, cookieValue)
                .httpOnly(true)
                .path("/")
                .maxAge(maxAge)
                .domain("www.billybang.me")
//                .secure(true)
//                .sameSite("None")
                .build();
    }
}
