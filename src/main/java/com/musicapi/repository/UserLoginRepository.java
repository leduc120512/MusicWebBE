package com.musicapi.repository;

import com.musicapi.model.UserLogin;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserLoginRepository extends JpaRepository<UserLogin, Long> {
    Page<UserLogin> findByUser_IdOrderByLoginTimeDesc(Long userId, Pageable pageable);
}
