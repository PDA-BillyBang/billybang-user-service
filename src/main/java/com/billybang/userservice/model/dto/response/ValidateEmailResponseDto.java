package com.billybang.userservice.model.dto.response;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ValidateEmailResponseDto {

    private Boolean existsByEmail;
}
