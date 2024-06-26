package com.billybang.userservice.model.dto.response;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignUpResponseDto {

    private String email;
    private LocalDate birthDate;
    private String nickname;
}
