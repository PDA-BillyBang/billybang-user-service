package com.billybang.userservice.model.dto.request;

import com.billybang.userservice.model.type.SignUpType;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignUpRequestDto {

    private SignUpType signUpType;
    private String email;
    private String password;
    private LocalDate birthDate;
    private String nickname;
    private UserInfoRequestDto userInfo;
}
