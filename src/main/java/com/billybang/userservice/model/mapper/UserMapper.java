package com.billybang.userservice.model.mapper;

import com.billybang.userservice.model.dto.request.SignUpRequestDto;
import com.billybang.userservice.model.dto.response.GetUserInfoResponseDto;
import com.billybang.userservice.model.dto.response.LoginResponseDto;
import com.billybang.userservice.model.dto.response.SignUpResponseDto;
import com.billybang.userservice.model.dto.response.UserResponseDto;
import com.billybang.userservice.model.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring",
        uses = UserQualifier.class,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    @Mapping(source = "password", target = "password", qualifiedByName = {"EncodePassword"})
    @Mapping(source = "signUpType", target = "signUpType", qualifiedByName = {"AssignSignUpType"})
    User toEntity(SignUpRequestDto dto);

    SignUpResponseDto toSignUpResponseDto(User entity);

    LoginResponseDto toLoginResponseDto(User entity);

    @Mapping(source = "id", target = "userId")
    UserResponseDto toUserResponseDto(User entity);

    @Mapping(source = "id", target = "userId")
    @Mapping(source = "userInfo", target = "userInfo", qualifiedByName = {"ToUserInfoResponseDto"})
    GetUserInfoResponseDto toGetUserInfoResponseDto(User entity);

}
