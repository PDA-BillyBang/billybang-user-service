package com.billybang.userservice.model.mapper;

import com.billybang.userservice.model.dto.response.UserInfoResponseDto;
import com.billybang.userservice.model.entity.UserInfo;
import com.billybang.userservice.model.type.SignUpType;
import lombok.RequiredArgsConstructor;
import org.mapstruct.Named;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserQualifier {

    private final PasswordEncoder passwordEncoder;
    private final UserInfoMapper userInfoMapper;

    @Named("EncodePassword")
    public String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }

    @Named("AssignSignUpType")
    public SignUpType assignSignUpType(SignUpType signUpType) {
        return SignUpType.BILLYBANG;
    }

    @Named("ToUserInfoResponseDto")
    public UserInfoResponseDto toUserInfoResponseDto(UserInfo userInfo) {
        return userInfoMapper.toDto(userInfo);
    }

}
