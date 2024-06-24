package com.billybang.userservice.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequestDto {

    @Schema(name = "email", example = "test1234@test.com")
    @NotNull
    private String email;

    @Schema(name = "password", example = "test1234")
    @NotNull
    private String password;
}
