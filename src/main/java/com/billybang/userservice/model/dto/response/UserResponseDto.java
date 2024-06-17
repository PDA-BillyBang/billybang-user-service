package com.billybang.userservice.model.dto.response;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDto {

    private Long userId;
    private String email;
    private LocalDate birthDate;
    private String nickname;
    private UserInfoResponseDto userInfo;

}
