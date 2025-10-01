package com.skynetauth.auth_service.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.skynetauth.auth_service.Enum.UserType;
import com.skynetauth.auth_service.dto.response.LoginResponse;
import com.skynetauth.auth_service.exceptions.InvalidEmailPasswordException;
import com.skynetauth.auth_service.exceptions.UserNotFoundException;
import com.skynetauth.auth_service.mapper.PermissionMapper;
import com.skynetauth.auth_service.models.User;
import com.skynetauth.auth_service.repositories.UserRepository;
import com.skynetauth.auth_service.utils.JwtUtil;

@Service
public class AuthenticationService {
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final PermissionMapper permissionMapper;

    public AuthenticationService(UserRepository userRepository, JwtUtil jwtUtil,
            PasswordEncoder passwordEncoder,PermissionMapper permissionMapper) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
        this.permissionMapper = permissionMapper;
    }

    public LoginResponse authenticate(String email, String password, UserType type) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(UserNotFoundException::new);
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new InvalidEmailPasswordException();
        }
        if (!user.getUserType().equals(type)) {
            throw new InvalidEmailPasswordException();
        }
        String token = jwtUtil.generateToken(user, null);
        LoginResponse response = new LoginResponse();
        response.setToken(token);
        response.setEmail(user.getEmail());
        response.setPermissions(permissionMapper.toPermissionDtos(user.getPermissions()));

        return response;
    }

    public LoginResponse impersonate(String email) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String adminEmail = authentication.getPrincipal().toString();
        var user = userRepository.findByEmail(email)
                .orElseThrow(UserNotFoundException::new);
        String token = jwtUtil.generateToken(user, adminEmail);
        LoginResponse response = new LoginResponse();
        response.setToken(token);
        response.setEmail(user.getEmail());
        response.setPermissions(permissionMapper.toPermissionDtos(user.getPermissions()));

        return response;
    }

}
