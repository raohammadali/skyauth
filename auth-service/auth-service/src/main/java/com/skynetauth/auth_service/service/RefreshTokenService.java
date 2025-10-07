package com.skynetauth.auth_service.service;
import java.time.Instant;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.skynetauth.auth_service.dto.response.LoginResponse;
import com.skynetauth.auth_service.mapper.PermissionMapper;
import com.skynetauth.auth_service.models.RefreshToken;
import com.skynetauth.auth_service.models.User;
import com.skynetauth.auth_service.repositories.RefreshTokenRepository;
import com.skynetauth.auth_service.utils.JwtUtil;

import jakarta.transaction.Transactional;

@Service
public class RefreshTokenService {

    @Value("${app.jwt.refresh-token-duration-ms}")
    private long REFRESH_TOKEN_DURATION_MS;

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtUtil jwtUtil;
    private final PermissionMapper permissionMapper;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository, JwtUtil jwtUtils, PermissionMapper permissionMapper) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.jwtUtil = jwtUtils;
        this.permissionMapper = permissionMapper;
    }

    public RefreshToken findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }
    @Transactional
    public RefreshToken createRefreshToken(User user) {
        RefreshToken token = refreshTokenRepository.findByUser(user).orElse(new RefreshToken());
        token.setUser(user);
        token.setExpiryDate(Instant.now().plusMillis(REFRESH_TOKEN_DURATION_MS));
        token.setToken(UUID.randomUUID().toString());
        return refreshTokenRepository.save(token);
    }

    public boolean isTokenExpired(RefreshToken token) {
        return token.getExpiryDate().isBefore(Instant.now());
    }

    public void delete(RefreshToken token) {
        refreshTokenRepository.delete(token);
    }

    public LoginResponse generateAccessToken(User user, String refreshToken) {
        String token = jwtUtil.generateToken(user, null);
        LoginResponse response = new LoginResponse();
        response.setToken(token);
        response.setEmail(user.getEmail());
        response.setRefreshToken(refreshToken);
        response.setPermissions(permissionMapper.toPermissionDtos(user.getPermissions()));
        return response;
    }
}
