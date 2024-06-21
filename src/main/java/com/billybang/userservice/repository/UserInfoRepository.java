package com.billybang.userservice.repository;

import com.billybang.userservice.model.entity.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserInfoRepository extends JpaRepository<UserInfo, Long> {

    @Query("SELECT u FROM UserInfo u WHERE u.user.id = :userId")
    Optional<UserInfo> findByUserId(Long userId);
}
