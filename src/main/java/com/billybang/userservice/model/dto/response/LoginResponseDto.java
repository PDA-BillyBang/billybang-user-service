package com.billybang.userservice.model.dto.response;

import com.billybang.userservice.model.type.SignUpType;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseDto {

    private SignUpType signUpType;
    private String nickname;
    private String email;
}
