package com.billybang.userservice.security.oauth;

import com.billybang.userservice.model.entity.User;
import com.billybang.userservice.model.type.SignUpType;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OAuthUserProfile {

    private String email;
    private String nickname;
    private String provider;

    public User toUserEntity() {
        return User.builder()
                .signUpType(SignUpType.getSignUpType(provider))
                .email(email)
                .nickname(nickname)
                .build();
    }
}
