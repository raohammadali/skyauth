package com.skynetauth.auth_service.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.skynetauth.auth_service.models.RefreshToken;
import com.skynetauth.auth_service.models.User;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    RefreshToken findByToken(String token);
    Optional<RefreshToken> findByUser(User user);
}