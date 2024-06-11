package com.billybang.userservice.controller;

import com.billybang.userservice.model.dto.request.LoginRequestDto;
import com.billybang.userservice.model.dto.request.SignUpRequestDto;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Objects;

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
        SignUpRequestDto requestDto = SignUpRequestDto.builder()
                .email("test1234@test.com")
                .password("test1234")
                .nickname("test")
                .birthDate(LocalDate.of(2000, 1, 1))
                .build();

        String request = objectMapper.writeValueAsString(requestDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/users/sign-up")
                        .contentType("application/json")
                        .content(request))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value(requestDto.getEmail()))
                .andExpect(jsonPath("$.nickname").value(requestDto.getNickname()));
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
                .andExpect(cookie().exists(JWTConstant.ACCESS_TOKEN))
                .andExpect(cookie().exists(JWTConstant.REFRESH_TOKEN));
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
                        .andReturn().getResponse().getCookie(JWTConstant.ACCESS_TOKEN)).getValue();

        mockMvc.perform(MockMvcRequestBuilders.get("/users/test")
                        .cookie(new Cookie(JWTConstant.ACCESS_TOKEN, accessToken)))
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
                        .andReturn().getResponse().getCookie(JWTConstant.ACCESS_TOKEN)).getValue();

        mockMvc.perform(MockMvcRequestBuilders.get("/users/logout")
                        .cookie(new Cookie(JWTConstant.ACCESS_TOKEN, accessToken)))
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$.accessToken").doesNotExist())
                .andExpect(jsonPath("$.refreshToken").doesNotExist());
    }


}