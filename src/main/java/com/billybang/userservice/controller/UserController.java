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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<TokenResponseDto> login(LoginRequestDto requestDto) {
        try {
            User user = userService.login(requestDto);
            return ResponseEntity.ok()
                    .body(TokenResponseDto.builder()
                            .accessToken(tokenService.genAccessTokenByEmail(user.getEmail()))
                            .refreshToken(tokenService.genRefreshTokenByEmail(user.getEmail()))
                            .build());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new CommonException(BError.FAIL, "Login");
        }
    }
}
