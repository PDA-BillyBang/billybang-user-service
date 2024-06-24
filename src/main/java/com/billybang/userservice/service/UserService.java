package com.billybang.userservice.service;

import com.billybang.userservice.exception.common.BError;
import com.billybang.userservice.exception.common.CommonException;
import com.billybang.userservice.model.dto.request.LoginRequestDto;
import com.billybang.userservice.model.dto.request.SignUpRequestDto;
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

    @Transactional(readOnly = true)
    public Boolean validateDuplicateNickname(String nickname) {
        return userRepository.existsByNickname(nickname);
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

    @Transactional(readOnly = true)
    public UserInfo getUserInfo() {
        Long userId = getLoginUserId();
        return userInfoRepository.findByUserId(userId)
                .orElseThrow(() -> new CommonException(BError.NOT_EXIST, "user info"));
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
    public void updateUserPassword(String password) {
        Long userId = getLoginUserId();
        User user = getUserById(userId);

        String encodedPassword = passwordEncoder.encode(password);
        if (user.getPassword().equals(encodedPassword)) {
            throw new CommonException(BError.MATCHES, "previous password", "new password");
        }
        user.updatePassword(encodedPassword);
    }

    @Transactional
    public void updateUserNickname(String nickname) {
        Long userId = getLoginUserId();
        User user = getUserById(userId);
        if (userRepository.existsByNickname(nickname)) {
            throw new CommonException(BError.EXIST, "nickname");
        }
        user.updateNickname(nickname);
    }

    @Transactional
    public void updateUserInfo(UserInfoRequestDto dto) {
        Long userId = getLoginUserId();
        userInfoRepository.findByUserId(userId)
                .map(userInfo -> {
                    userInfo.update(dto);
                    return userInfo;
                })
                .orElseThrow(() -> new CommonException(BError.NOT_EXIST, "user info"));
    }

    @Transactional
    public void deleteUser() {
        Long userId = getLoginUserId();
        User user = getUserById(userId);
        userRepository.delete(user);
    }

    @Transactional
    protected User setUser(SignUpRequestDto dto) {

        if (userRepository.existsByNickname(dto.getNickname())) {
            throw new CommonException(BError.EXIST, "nickname");
        }
        return userMapper.toEntity(dto);
    }
}
