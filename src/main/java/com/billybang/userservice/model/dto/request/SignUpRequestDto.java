package com.billybang.userservice.model.dto.request;

import com.billybang.userservice.model.type.SignUpType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignUpRequestDto {

    @Schema(name = "signUpType", example = "이 필드는 필요없음")
    private SignUpType signUpType;
    @Schema(name = "email", example = "test1234@test.com")
    private String email;
    @Schema(name = "password", example = "test1234")
    private String password;
    @Schema(name = "birthDate", example = "2000-01-01")
    private LocalDate birthDate;
    @Schema(name = "nickname", example = "빌려방")
    private String nickname;
}
