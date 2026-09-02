package com.yorimichi.yorimichi.domain.user.repository;

import org.apache.ibatis.annotations.Mapper;

import com.yorimichi.yorimichi.domain.user.entity.User;

import java.util.Optional;

@Mapper
public interface UserMapper {

    void save(User user);

    Optional<User> findById(Long memberId);

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);
}
