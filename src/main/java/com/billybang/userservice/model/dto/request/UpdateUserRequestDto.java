package com.billybang.userservice.model.dto.request;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserRequestDto {

    private String password;
    private LocalDate birthDate;
    private String nickname;
    private UserInfoRequestDto userInfo;
}
