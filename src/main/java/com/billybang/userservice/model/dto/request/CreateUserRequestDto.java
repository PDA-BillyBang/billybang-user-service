package com.billybang.userservice.model.dto.request;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserRequestDto {

    private String email;
    private String password;
    private LocalDate birthDate;
    private String nickname;
}
