package com.billybang.userservice.model.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePwdRequestDto {

    @NotNull
    private String password;
}
