package com.skynetauth.auth_service.service;

import java.util.ArrayList;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.skynetauth.auth_service.dto.request.SignupRequest;
import com.skynetauth.auth_service.dto.request.UpdateRequest;
import com.skynetauth.auth_service.exceptions.EmailAlreadyUsedException;
import com.skynetauth.auth_service.exceptions.InvalidIDException;
import com.skynetauth.auth_service.exceptions.UserNotFoundException;
import com.skynetauth.auth_service.models.Distribution;
import com.skynetauth.auth_service.models.Permission;
import com.skynetauth.auth_service.models.Role;
import com.skynetauth.auth_service.models.User;
import com.skynetauth.auth_service.repositories.DistributionRepository;
import com.skynetauth.auth_service.repositories.PermissionRepository;
import com.skynetauth.auth_service.repositories.RoleRepository;
import com.skynetauth.auth_service.repositories.UserRepository;
import com.skynetauth.auth_service.utils.HashIdUtil;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final DistributionRepository distributionRepository;
    private final HashIdUtil hashIdUtil;

    public CustomUserDetailsService(
            UserRepository userRepository,
            RoleRepository roleRepository,
            PermissionRepository permissionRepository,
            PasswordEncoder passwordEncoder,
            DistributionRepository distributionRepository,
            HashIdUtil hashIdUtil) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
        this.passwordEncoder = passwordEncoder;
        this.distributionRepository = distributionRepository;
        this.hashIdUtil = hashIdUtil;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(""));

        Set<GrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toSet());

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                authorities);
    }

    @Transactional
    public User createUser(SignupRequest signupRequest) throws EmailAlreadyUsedException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getPrincipal().toString();
        List<Distribution> adminDistributions = userRepository.findByEmail(email)
                .orElseThrow(UserNotFoundException::new).getDistributions();

        if (userRepository.existsByEmail(signupRequest.getEmail())) {
            throw new EmailAlreadyUsedException();
        }
        List<Role> roles = roleRepository
                .findAllById(signupRequest.getRoles().stream().map(hashIdUtil::decodeId).toList());
        List<Distribution> distributions = distributionRepository
                .findAllById(signupRequest.getDistributions().stream().map(hashIdUtil::decodeId).toList());

        if (signupRequest.getDistributions().size() > distributions.size()
                || distributions.size() > adminDistributions.size()) {
            throw new InvalidIDException("distribution");
        }

        if (signupRequest.getRoles().size() > roles.size()) {
            throw new InvalidIDException("role");
        }

        /**
         * The admin has given a list of distributions the user will be linked to.
         * I want to go through the distributions of the user to create and see if all
         * of them are in the distributions of the admin
         */
        List<Distribution> filteredDist = new ArrayList<>();

        User user = new User();
        user.setFirstName(signupRequest.getFirstName());
        user.setLastName(signupRequest.getLastName());
        user.setPhone(signupRequest.getPhone());
        user.setEmail(signupRequest.getEmail());
        user.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
        user.setRoles(roles);
        user.setUserType(signupRequest.getUserType());

        if (signupRequest.getPermissions() == null || signupRequest.getPermissions().isEmpty()) {
            user.setPermissions((roles.stream()
                    .flatMap(role -> role.getPermissions().stream())
                    .toList()));
        } else {
            List<Long> permissionIds = signupRequest.getPermissions().stream().map(hashIdUtil::decodeId).toList();
            List<Permission> userPermissions = permissionRepository.findAllById(permissionIds);
            if (userPermissions.size() != signupRequest.getPermissions().size()) {
                throw new InvalidIDException("permission");
            }
            user.setPermissions(userPermissions);
        }

        User savedUser = userRepository.save(user);
        for (Distribution dist : distributions) {
            if (!adminDistributions.contains(dist)) {
                throw new InvalidIDException("distribution");
            }

            filteredDist.add(dist);
            dist.getUsers().add(savedUser);
            dist.setUsers(dist.getUsers());
        }
        savedUser.setDistributions(filteredDist);
        distributionRepository.saveAll(filteredDist);
        return userRepository.save(savedUser);
    }

    public Page<User> getUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    public User editUser(UpdateRequest dto, Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(UserNotFoundException::new);

        if (userRepository.existsByEmail(dto.getEmail()) && !user.getEmail().equals(dto.getEmail())) {
            throw new EmailAlreadyUsedException();
        }

        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setProfileImageUrl(dto.getProfileImageUrl());
        user.setUserType(dto.getUserType());

        List<Role> roles;
        List<Permission> permissions;
        List<Distribution> distributions;
        try {
            roles = roleRepository.findAllById(dto.getRoles().stream().map(hashIdUtil::decodeId).toList());
        } catch (Exception e) {
            throw new InvalidIDException("role");
        }

        if (dto.getPermissions() == null || dto.getPermissions().isEmpty()) {
            permissions = roles.stream()
                    .flatMap(role -> role.getPermissions().stream())
                    .toList();
        } else {
            try {
                permissions = permissionRepository
                        .findAllById(dto.getPermissions().stream().map(hashIdUtil::decodeId).toList());
                if (permissions.size() < dto.getPermissions().size()) {
                    throw new InvalidIDException("permission");
                }
            } catch (Exception e) {
                throw new InvalidIDException("permission");
            }
        }

        try {
            distributions = distributionRepository
                    .findAllById(dto.getDistributions().stream().map(hashIdUtil::decodeId).toList());
        } catch (Exception e) {
            throw new InvalidIDException("distribution");
        }

        if (roles.size() < dto.getRoles().size()) {
            throw new InvalidIDException("role");
        }
        
        if (distributions.size() < dto.getDistributions().size()) {
            throw new InvalidIDException("distribution");
        }

        user.setRoles(roles);
        user.setPermissions(permissions);
        for (Distribution dist : distributions) {
            if (!dist.getUsers().contains(user)) {
                dist.getUsers().add(user);
            }
        }
        user.setDistributions(distributions);
        distributionRepository.saveAll(distributions);
        return userRepository.save(user);
    }

}
