package com.billybang.userservice.model.mapper;

import com.billybang.userservice.model.dto.request.UserInfoRequestDto;
import com.billybang.userservice.model.dto.response.UserInfoResponseDto;
import com.billybang.userservice.model.entity.UserInfo;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface UserInfoMapper {

    UserInfo toEntity(UserInfoRequestDto dto);

    UserInfoResponseDto toDto(UserInfo entity);
}
