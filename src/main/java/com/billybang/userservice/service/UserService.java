package com.billybang.userservice.service;

import com.billybang.userservice.exception.common.BError;
import com.billybang.userservice.exception.common.CommonException;
import com.billybang.userservice.model.dto.request.LoginRequestDto;
import com.billybang.userservice.model.dto.request.SignUpRequestDto;
import com.billybang.userservice.model.entity.User;
import com.billybang.userservice.model.mapper.UserInfoMapper;
import com.billybang.userservice.model.mapper.UserMapper;
import com.billybang.userservice.repository.UserInfoRepository;
import com.billybang.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserInfoRepository userInfoRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final UserInfoMapper userInfoMapper;

    @Transactional(readOnly = true)
    public User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new CommonException(BError.NOT_EXIST, "user"));
    }

    @Transactional
    public User signUp(SignUpRequestDto dto) {
        try {
            if (userRepository.existsByEmail(dto.getEmail())) {
                throw new CommonException(BError.EXIST, "email");
            }
            User user = userRepository.save(setUser(dto));
            userInfoRepository.save(user.getUserInfo());
            return user;
        } catch (Exception e) {
            log.error(e.getMessage());
            log.debug(e.getMessage(), e);
            throw new CommonException(BError.FAIL, "sign up");
        }
    }

    @Transactional
    public User login(LoginRequestDto dto) {
        User user = getUser(dto.getEmail());
        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new CommonException(BError.NOT_MATCH, "password");
        }
        return user;
    }

    @Transactional
    protected User setUser(SignUpRequestDto dto) {

        if (userRepository.existsByNickname(dto.getNickname())) {
            throw new CommonException(BError.EXIST, "nickname");
        }
        User user = userMapper.toEntity(dto);
        user.addUserInfo(userInfoMapper.toEntity(dto.getUserInfo()));
        return user;
    }
}
