package com.billybang.userservice.model.entity;

import com.billybang.userservice.model.type.SignUpType;
import com.billybang.userservice.model.vo.BaseTime;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

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

    @Enumerated(EnumType.STRING)
    private SignUpType signUpType;

    @Column(unique = true, nullable = false)
    private String email;

    private String password;

    private LocalDate birthDate;

    @Column
    private String nickname;

    @OneToOne(mappedBy = "user")
    private UserInfo userInfo;

    public void addUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
        userInfo.setUser(this);
    }

    // TODO: Implement update method
    public User update(String email, String nickname) {
        this.email = email;
        this.nickname = nickname;
        return this;
    }

    public void updatePassword(String password) {
        this.password = password;
    }

    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }

}
