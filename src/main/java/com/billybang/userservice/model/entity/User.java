package com.billybang.userservice.model.entity;

import com.billybang.userservice.model.type.SignupType;
import com.billybang.userservice.model.vo.BaseTime;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "users")
public class User extends BaseTime {

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

    // TODO: Implement update method
    public User update(String email, String nickname) {
        this.email = email;
        this.nickname = nickname;
        return this;
    }

}
