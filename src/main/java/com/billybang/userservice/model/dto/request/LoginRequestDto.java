package com.billybang.userservice.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequestDto {

    @Schema(name = "email", example = "test1234@test.com")
    private String email;
    @Schema(name = "password", example = "test1234")
    private String password;
}
