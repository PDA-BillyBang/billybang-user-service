package com.billybang.userservice.api;

import com.billybang.userservice.model.dto.request.*;
import com.billybang.userservice.model.dto.response.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
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
    ResponseEntity<ApiResult<SignUpResponseDto>> signUp(@RequestBody @Valid SignUpRequestDto requestDto);

    @Operation(summary = "회원 추가 정보 등록", description = "대출 상품 추천을 위한 추가 정보를 등록합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Created"),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "405", description = "Method Not Allowed")
    })
    @PostMapping("/user-info")
    ResponseEntity<ApiResult<UserInfoResponseDto>> addUserInfo(@RequestBody @Valid UserInfoRequestDto requestDto);

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
    ResponseEntity<ApiResult<LoginResponseDto>> login(@RequestBody @Valid LoginRequestDto requestDto);

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
    @GetMapping("/user-info")
    ResponseEntity<ApiResult<GetUserInfoResponseDto>> getUserInfo();

    @Operation(summary = "비밀번호 수정", description = "비밀번호를 수정합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "405", description = "Method Not Allowed")
    })
    @PutMapping("/password")
    ResponseEntity<ApiResult<?>> updateUserPassword(@RequestBody @Valid UpdatePwdRequestDto requestDto);

    @Operation(summary = "닉네임 수정", description = "닉네임을 수정합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "405", description = "Method Not Allowed")
    })
    @PutMapping("/nickname")
    ResponseEntity<ApiResult<?>> updateUserNickname(@RequestBody @Valid UpdateNicknameRequestDto requestDto);

    @Operation(summary = "회원정보 수정", description = "회원정보를 수정합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "405", description = "Method Not Allowed")
    })
    @PutMapping("/user-info")
    ResponseEntity<ApiResult<UserInfoResponseDto>> updateUserInfo(@RequestBody @Valid UserInfoRequestDto requestDto);

    @Operation(summary = "회원 탈퇴", description = "회원 탈퇴를 진행합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "405", description = "Method Not Allowed")
    })
    @DeleteMapping
    ResponseEntity<ApiResult<?>> deleteUser();

    @Operation(summary = "이메일 중복 확인", description = "이메일 중복 확인을 진행합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "405", description = "Method Not Allowed")
    })
    @GetMapping("/validate-email")
    ResponseEntity<ApiResult<ValidateEmailResponseDto>> validateEmail(@RequestParam String email);

    @Operation(summary = "닉네임 중복 확인", description = "닉네임 중복 확인을 진행합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "405", description = "Method Not Allowed")
    })
    @GetMapping("/validate-nickname")
    ResponseEntity<ApiResult<ValidateNicknameResponseDto>> validateNickname(@RequestParam String nickname);

    @Operation(summary = "토큰의 유효성 확인", description = "토큰의 유효성을 확인합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "405", description = "Method Not Allowed")
    })
    @GetMapping("/validate-token")
    ResponseEntity<ApiResult<ValidateTokenResponseDto>> validateToken();
}
