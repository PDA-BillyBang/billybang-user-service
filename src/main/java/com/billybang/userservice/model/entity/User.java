package com.billybang.userservice.model.entity;

import com.billybang.userservice.model.dto.request.SignUpRequestDto;
import com.billybang.userservice.model.type.SignupType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "users")
public class User {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    private SignupType signupType;

    @Column(unique = true)
    private String email;

    private String password;

    private LocalDate birthDate;

    @Column(unique = true)
    private String nickname;

    public static User create(User inputUser) {
        return User.builder()
                .email(inputUser.getEmail())
                .password(inputUser.getPassword())
                .birthDate(inputUser.getBirthDate())
                .nickname(inputUser.getNickname())
                .build();
    }

}
