package com.billybang.userservice.service;

import com.billybang.userservice.model.dto.request.SignUpRequestDto;
import com.billybang.userservice.model.entity.User;
import com.billybang.userservice.model.mapper.UserMapper;
import com.billybang.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Transactional(readOnly = true)
    public User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User Not Found"));
    }

    @Transactional
    public User signup(SignUpRequestDto dto) {
        try {
            if (userRepository.existsByEmail(dto.getEmail())) {
                throw new RuntimeException("User Already Exist");
            }
            return userRepository.save(setUser(dto));
        } catch (Exception e) {
            log.error(e.getMessage());
            log.debug(e.getMessage(), e);
            throw new RuntimeException("User Create Fail");
        }
    }

    @Transactional
    protected User setUser(SignUpRequestDto dto) {

        if (userRepository.existsByNickname(dto.getNickname())) {
            throw new RuntimeException("Nickname Already Exist");
        }
        return User.create(userMapper.toEntity(dto));
    }
}
