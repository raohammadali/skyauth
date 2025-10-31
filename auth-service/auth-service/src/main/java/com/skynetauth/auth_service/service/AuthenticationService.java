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

    /**
     * Constructs an AuthenticationService with the required collaborators.
     *
     * Assigns the provided repository, JWT utility, password encoder, and permission mapper
     * to the service for user lookup, token generation, password verification, and permission mapping.
     */
    public AuthenticationService(UserRepository userRepository, JwtUtil jwtUtil,
            PasswordEncoder passwordEncoder,PermissionMapper permissionMapper) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
        this.permissionMapper = permissionMapper;
    }

    /**
     * Authenticate a user with the provided credentials and produce a login response containing a JWT and permissions.
     *
     * @param email    the user's email address used to locate the account
     * @param password the plain-text password to verify against the stored credentials
     * @param type     the expected UserType for the account; authentication fails if the user's type does not match
     * @return         a LoginResponse containing the generated JWT token, the user's email, and the user's permissions
     * @throws UserNotFoundException         if no user exists for the given email
     * @throws InvalidEmailPasswordException if the password does not match or the user's type does not match the provided type
     */
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

    /**
     * Creates an impersonation login response for the user identified by the given email.
     *
     * @param email the target user's email to impersonate
     * @return a LoginResponse containing a JWT representing the impersonated user, the user's email, and their permissions
     * @throws UserNotFoundException if no user exists with the given email
     */
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
