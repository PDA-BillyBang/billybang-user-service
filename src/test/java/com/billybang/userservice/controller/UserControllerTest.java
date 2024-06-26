package com.billybang.userservice.controller;

import com.billybang.userservice.model.dto.request.LoginRequestDto;
import com.billybang.userservice.model.dto.request.SignUpRequestDto;
import com.billybang.userservice.model.dto.request.UserInfoRequestDto;
import com.billybang.userservice.model.type.CompanySize;
import com.billybang.userservice.model.type.Occupation;
import com.billybang.userservice.security.AuthConstant;
import com.billybang.userservice.security.jwt.JWTConstant;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Objects;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    @Transactional
    @DisplayName("회원가입 API 테스트")
    void signUp() throws Exception {

        SignUpRequestDto signUpRequestDto = SignUpRequestDto.builder()
                .email("test1234@test.com")
                .password("test1234")
                .nickname("test")
                .birthDate(LocalDate.of(2000, 1, 1))
                .build();

        String request = objectMapper.writeValueAsString(signUpRequestDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/users/sign-up")
                        .contentType("application/json")
                        .content(request))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.response.email").value(signUpRequestDto.getEmail()))
                .andExpect(jsonPath("$.response.nickname").value(signUpRequestDto.getNickname()));
    }

    @Test
    @Transactional
    @DisplayName("로그인 API 테스트")
    void login() throws Exception {
        LoginRequestDto requestDto = LoginRequestDto.builder()
                .email(AuthConstant.ADMIN_USER)
                .password(AuthConstant.ADMIN_PWD)
                .build();

        String request = objectMapper.writeValueAsString(requestDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/users/login")
                        .contentType("application/json")
                        .content(request))
                .andExpect(status().isOk())
                .andExpect(cookie().exists(JWTConstant.ACCESS_TOKEN_NAME))
                .andExpect(cookie().exists(JWTConstant.REFRESH_TOKEN_NAME));
    }

    @Test
    @Transactional
    @DisplayName("로그인 후에 쿠키에 access token 을 담아서 요청을 보내면 인증이 되어야 한다.")
    void authenticationAfterLogin() throws Exception {
        LoginRequestDto requestDto = LoginRequestDto.builder()
                .email(AuthConstant.ADMIN_USER)
                .password(AuthConstant.ADMIN_PWD)
                .build();

        String request = objectMapper.writeValueAsString(requestDto);

        String accessToken = Objects.requireNonNull(
                mockMvc.perform(MockMvcRequestBuilders.post("/users/login")
                                .contentType("application/json")
                                .content(request))
                        .andExpect(status().isOk())
                        .andReturn().getResponse().getCookie(JWTConstant.ACCESS_TOKEN_NAME)).getValue();

        mockMvc.perform(MockMvcRequestBuilders.get("/users/test")
                        .cookie(new Cookie(JWTConstant.ACCESS_TOKEN_NAME, accessToken)))
                .andExpect(status().isOk());
    }

    @Test
    @Transactional
    @DisplayName("로그아웃 API 테스트")
    void logout() throws Exception {
        LoginRequestDto requestDto = LoginRequestDto.builder()
                .email(AuthConstant.ADMIN_USER)
                .password(AuthConstant.ADMIN_PWD)
                .build();

        String request = objectMapper.writeValueAsString(requestDto);

        String accessToken = Objects.requireNonNull(
                mockMvc.perform(MockMvcRequestBuilders.post("/users/login")
                                .contentType("application/json")
                                .content(request))
                        .andExpect(status().isOk())
                        .andReturn().getResponse().getCookie(JWTConstant.ACCESS_TOKEN_NAME)).getValue();

        mockMvc.perform(MockMvcRequestBuilders.get("/users/logout")
                        .cookie(new Cookie(JWTConstant.ACCESS_TOKEN_NAME, accessToken)))
                .andExpect(status().isNoContent());
    }

    @Test
    @Transactional
    @DisplayName("회원정보 수정 테스트")
    void updateUserInfo() throws Exception {

        LoginRequestDto loginRequestDto = LoginRequestDto.builder()
                .email(AuthConstant.ADMIN_USER)
                .password(AuthConstant.ADMIN_PWD)
                .build();

        String loginRequest = objectMapper.writeValueAsString(loginRequestDto);

        MockHttpServletResponse loginResponse = Objects.requireNonNull(
                mockMvc.perform(MockMvcRequestBuilders.post("/users/login")
                                .contentType("application/json")
                                .content(loginRequest))
                        .andExpect(status().isOk())
                        .andReturn().getResponse());
        String accessToken = Objects.requireNonNull(loginResponse.getCookie(JWTConstant.ACCESS_TOKEN_NAME)).getValue();
        String refreshToken = Objects.requireNonNull(loginResponse.getCookie(JWTConstant.REFRESH_TOKEN_NAME)).getValue();
        String userId = Objects.requireNonNull(loginResponse.getCookie(AuthConstant.USER_ID)).getValue();

        UserInfoRequestDto userInfoRequestDto = UserInfoRequestDto.builder()
                .occupation(Occupation.GENERAL)
                .companySize(CompanySize.LARGE)
                .employmentDuration(24)
                .individualIncome(3000)
                .totalMarriedIncome(5000)
                .childrenCount(1)
                .isForeign(false)
                .isFirstHouseBuyer(true)
                .isMarried(true)
                .yearOfMarriage(2010)
                .hasOtherLoans(false)
                .build();

        String userInfoRequest = objectMapper.writeValueAsString(userInfoRequestDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/users/user-info")
                        .cookie(new Cookie(JWTConstant.ACCESS_TOKEN_NAME, accessToken))
                        .cookie(new Cookie(JWTConstant.REFRESH_TOKEN_NAME, refreshToken))
                        .cookie(new Cookie(AuthConstant.USER_ID, userId))
                        .contentType("application/json")
                        .content(userInfoRequest));

        UserInfoRequestDto updateDto = UserInfoRequestDto.builder()
                .occupation(Occupation.FINANCE)
                .companySize(CompanySize.LARGE)
                .employmentDuration(24)
                .individualIncome(3000)
                .totalMarriedIncome(5000)
                .childrenCount(1)
                .isForeign(false)
                .isFirstHouseBuyer(true)
                .isMarried(true)
                .yearOfMarriage(2010)
                .hasOtherLoans(false)
                .build();

        String updateRequest = objectMapper.writeValueAsString(updateDto);

        mockMvc.perform(MockMvcRequestBuilders.put("/users/user-info", 1)
                        .cookie(new Cookie(JWTConstant.ACCESS_TOKEN_NAME, accessToken))
                        .cookie(new Cookie(JWTConstant.REFRESH_TOKEN_NAME, refreshToken))
                        .cookie(new Cookie(AuthConstant.USER_ID, userId))
                        .contentType("application/json")
                        .content(updateRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response.occupation").value(updateDto.getOccupation().name()))
                .andDo(print());

    }

    @Test
    @DisplayName("로그인 후 추가 정보 등록 API 테스트")
    void addUserInfo() throws Exception {
        LoginRequestDto loginRequestDto = LoginRequestDto.builder()
                .email(AuthConstant.ADMIN_USER)
                .password(AuthConstant.ADMIN_PWD)
                .build();

        String request = objectMapper.writeValueAsString(loginRequestDto);

        MockHttpServletResponse response = Objects.requireNonNull(
                mockMvc.perform(MockMvcRequestBuilders.post("/users/login")
                                .contentType("application/json")
                                .content(request))
                        .andExpect(status().isOk())
                        .andReturn().getResponse());

        String accessToken = Objects.requireNonNull(response.getCookie(JWTConstant.ACCESS_TOKEN_NAME)).getValue();
        String refreshToken = Objects.requireNonNull(response.getCookie(JWTConstant.REFRESH_TOKEN_NAME)).getValue();
        String userId = Objects.requireNonNull(response.getCookie(AuthConstant.USER_ID)).getValue();


        UserInfoRequestDto userInfoRequestDto = UserInfoRequestDto.builder()
                .occupation(Occupation.GENERAL)
                .companySize(CompanySize.LARGE)
                .employmentDuration(24)
                .individualIncome(3000)
                .totalMarriedIncome(5000)
                .childrenCount(1)
                .isForeign(false)
                .isFirstHouseBuyer(true)
                .isMarried(true)
                .yearOfMarriage(2010)
                .hasOtherLoans(false)
                .build();

        String requestUserInfo = objectMapper.writeValueAsString(userInfoRequestDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/users/user-info")
                        .cookie(new Cookie(JWTConstant.ACCESS_TOKEN_NAME, accessToken))
                        .cookie(new Cookie(JWTConstant.REFRESH_TOKEN_NAME, refreshToken))
                        .cookie(new Cookie(AuthConstant.USER_ID, userId))
                        .contentType("application/json")
                        .content(requestUserInfo))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.response.occupation").value(userInfoRequestDto.getOccupation().name()));
    }
}