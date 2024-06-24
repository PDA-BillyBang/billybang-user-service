package com.billybang.userservice.model.dto.request;

import com.billybang.userservice.model.type.SignUpType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
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
    @NotNull
    private String email;

    @Schema(name = "password", example = "test1234")
    @NotNull
    private String password;

    @Schema(name = "birthDate", example = "2000-01-01")
    @NotNull
    private LocalDate birthDate;

    @Schema(name = "nickname", example = "빌려방")
    @NotNull
    private String nickname;
}
