package com.billybang.userservice.security.oauth;

import com.billybang.userservice.model.entity.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OAuthUserProfile {

    private String email;
    private String nickname;

    public User toUser() {
        return User.builder()
                .email(email)
                .nickname(nickname)
                .build();
    }
}
