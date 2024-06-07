package com.billybang.userservice.service;

import com.billybang.userservice.model.dto.request.SignUpRequestDto;
import com.billybang.userservice.model.entity.User;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Test
    void signup() {
        SignUpRequestDto dto = SignUpRequestDto.builder()
                .email("billybang@test.com")
                .password("p@ssw0rd")
                .birthDate(LocalDate.of(2000, 1,1))
                .nickname("billy")
                .build();

        User user = userService.signup(dto);

        assertThat(user.getEmail()).isEqualTo(dto.getEmail());
        assertThat(user.getNickname()).isEqualTo(dto.getNickname());
    }
}