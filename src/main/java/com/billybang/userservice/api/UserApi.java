package com.billybang.userservice.api;

import com.billybang.userservice.model.dto.request.LoginRequestDto;
import com.billybang.userservice.model.dto.request.SignUpRequestDto;
import com.billybang.userservice.model.dto.request.UpdateUserRequestDto;
import com.billybang.userservice.model.dto.response.LoginResponseDto;
import com.billybang.userservice.model.dto.response.SignUpResponseDto;
import com.billybang.userservice.model.dto.response.UserResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "User API", description = "유저 API")
@RequestMapping("/users")
public interface UserApi {

    @Operation(summary = "회원가입", description = "이메일, 비밀번호, 닉네임 등을 입력받고 회원가입을 진행합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Created"),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "405", description = "Method Not Allowed")
    })
    @PostMapping("/sign-up")
    ResponseEntity<ApiResult<SignUpResponseDto>> signUp(@RequestBody SignUpRequestDto requestDto);

    @Operation(summary = "로그인", description = "이메일과 비밀번호를 통해 로그인을 합니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    headers = {@Header(name = "Set-Cookie", description = "access_token 과 refresh_token 정보를 포함")},
                    description = "OK"),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "405", description = "Method Not Allowed")
    })
    @PostMapping("/login")
    ResponseEntity<ApiResult<LoginResponseDto>> login(@RequestBody LoginRequestDto requestDto);

    @Operation(summary = "로그아웃", description = "로그아웃을 진행합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "No Content"),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "405", description = "Method Not Allowed")
    })
    @PostMapping("/logout")
    ResponseEntity<?> logout();

    @Operation(summary = "회원정보 조회", description = "회원정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "405", description = "Method Not Allowed")
    })
    @GetMapping("/{userId}")
    ResponseEntity<ApiResult<UserResponseDto>> getUser(@PathVariable Long userId);

    @Operation(summary = "회원정보 수정", description = "회원정보를 수정합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "405", description = "Method Not Allowed")
    })
    @PutMapping("/{userId}")
    ResponseEntity<ApiResult<?>> update(@PathVariable Long userId, @RequestBody UpdateUserRequestDto requestDto);

    @Operation(summary = "테스트", description = "")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "405", description = "Method Not Allowed")
    })
    @GetMapping("/test")
    ResponseEntity<String> test();
}
