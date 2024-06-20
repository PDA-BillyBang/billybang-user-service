package com.billybang.userservice.service;

import com.billybang.userservice.exception.common.BError;
import com.billybang.userservice.exception.common.CommonException;
import com.billybang.userservice.model.dto.request.LoginRequestDto;
import com.billybang.userservice.model.dto.request.SignUpRequestDto;
import com.billybang.userservice.model.dto.request.UpdateUserRequestDto;
import com.billybang.userservice.model.dto.request.UserInfoRequestDto;
import com.billybang.userservice.model.entity.User;
import com.billybang.userservice.model.entity.UserInfo;
import com.billybang.userservice.model.mapper.UserInfoMapper;
import com.billybang.userservice.model.mapper.UserMapper;
import com.billybang.userservice.repository.UserInfoRepository;
import com.billybang.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

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
    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new CommonException(BError.NOT_EXIST, "user"));
    }

    @Transactional(readOnly = true)
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new CommonException(BError.NOT_EXIST, "user"));
    }

    @Transactional
    public Long getLoginUserId() {
        String userEmail = Optional.ofNullable((String) SecurityContextHolder.getContext().getAuthentication().getPrincipal())
                .orElseThrow(() -> new CommonException(BError.NOT_VALID, "user"));
        return getUserByEmail(userEmail).getId();
    }

    @Transactional(readOnly = true)
    public Boolean validateDuplicateEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Transactional
    public User signUp(SignUpRequestDto dto) {
        try {
            if (userRepository.existsByEmail(dto.getEmail())) {
                throw new CommonException(BError.EXIST, "email");
            }
            return userRepository.save(setUser(dto));
        } catch (Exception e) {
            log.error(e.getMessage());
            log.debug(e.getMessage(), e);
            throw new CommonException(BError.FAIL, "sign up");
        }
    }

    @Transactional
    public UserInfo addUserInfo(UserInfoRequestDto dto) {
        Long loginUserId = getLoginUserId();
        User user = getUserById(loginUserId);
        UserInfo userInfo = userInfoMapper.toEntity(dto);
        userInfoRepository.save(userInfo);
        user.addUserInfo(userInfo);
        return userInfo;
    }

    @Transactional
    public User login(LoginRequestDto dto) {
        User user = getUserByEmail(dto.getEmail());
        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new CommonException(BError.NOT_MATCH, "password");
        }
        return user;
    }

    @Transactional
    public void updateUser(Long userId, UpdateUserRequestDto dto) {
        userRepository.findById(userId)
                .map(user -> {
                    user.update(dto);
                    return user;
                })
                .orElseThrow(() -> new CommonException(BError.NOT_EXIST, "user"));
    }

    @Transactional
    protected User setUser(SignUpRequestDto dto) {

        if (userRepository.existsByNickname(dto.getNickname())) {
            throw new CommonException(BError.EXIST, "nickname");
        }
        return userMapper.toEntity(dto);
    }
}
