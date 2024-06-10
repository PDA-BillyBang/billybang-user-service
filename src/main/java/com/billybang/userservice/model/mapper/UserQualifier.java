package com.billybang.userservice.model.mapper;

import com.billybang.userservice.model.type.SignUpType;
import org.mapstruct.Named;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class UserQualifier {

    private final PasswordEncoder passwordEncoder;

    public UserQualifier(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Named("EncodePassword")
    public String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }

    @Named("AssignSignUpType")
    public SignUpType assignSignUpType(SignUpType signUpType) {
        return SignUpType.BILLYBANG;
    }

}
