package com.billybang.userservice.service;

import com.billybang.userservice.model.dto.request.LoginRequestDto;
import com.billybang.userservice.model.dto.request.SignUpRequestDto;
import com.billybang.userservice.model.dto.request.UpdateUserRequestDto;
import com.billybang.userservice.model.dto.request.UserInfoRequestDto;
import com.billybang.userservice.model.entity.User;
import com.billybang.userservice.model.type.CompanySize;
import com.billybang.userservice.model.type.Occupation;
import com.billybang.userservice.model.type.SignUpType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static com.billybang.userservice.security.AuthConstant.ADMIN_USER;
import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Test
    @Transactional
    void signup() {
        SignUpRequestDto dto = SignUpRequestDto.builder()
                .email("billybang@test.com")
                .password("p@ssw0rd")
                .birthDate(LocalDate.of(2000, 1,1))
                .nickname("billy")
                .build();

        User user = userService.signUp(dto);

        assertThat(user.getEmail()).isEqualTo(dto.getEmail());
        assertThat(user.getNickname()).isEqualTo(dto.getNickname());
        assertThat(user.getSignUpType().name()).isEqualTo(SignUpType.BILLYBANG.name());
    }

    @Test
    @Transactional
    void login() {
        SignUpRequestDto signUpRequestDto = SignUpRequestDto.builder()
                .email("billybang@test.com")
                .password("p@ssw0rd")
                .birthDate(LocalDate.of(2000, 1,1))
                .nickname("billy")
                .build();
        userService.signUp(signUpRequestDto);
        LoginRequestDto loginRequestDto = LoginRequestDto.builder()
                .email("billybang@test.com")
                .password("p@ssw0rd")
                .build();

        User user = userService.login(loginRequestDto);

        assertThat(user.getEmail()).isEqualTo(loginRequestDto.getEmail());
    }

    @Test
    @Transactional
    void updateUser() {
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

        userService.updateUserInfo(userInfoRequestDto);
        User user = userService.getUserByEmail(ADMIN_USER);

        assertThat(user.getUserInfo().getOccupation()).isEqualTo(userInfoRequestDto.getOccupation());
    }

}