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

    /**
     * Creates a CustomUserDetailsService with the required repositories, password encoder, distribution repository, and hash ID utility.
     *
     * @param userRepository the repository for user persistence and retrieval
     * @param roleRepository the repository for role persistence and retrieval
     * @param permissionRepository the repository for permission persistence and retrieval
     * @param passwordEncoder the password encoder used to encode and verify user passwords
     * @param distributionRepository the repository for distribution persistence and retrieval
     * @param hashIdUtil utility for encoding and decoding hashed IDs used by the service
     */
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

    /**
     * Load user details by email for Spring Security authentication.
     *
     * @param email the email address of the user to load
     * @return a UserDetails whose username is the user's email, password is the stored password, and authorities are derived from the user's roles
     * @throws UsernameNotFoundException if no user exists with the given email
     */
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

    /**
     * Create a new user from the provided signup request and persist it with its roles,
     * permissions, and distributions.
     *
     * The method decodes role, permission, and distribution IDs from the request, validates
     * them (including that requested distributions are permitted for the current authenticated
     * admin), encodes the password, links the user to distributions, and saves the user and
     * affected distributions.
     *
     * @param signupRequest contains the new user's details (first name, last name, phone, email,
     *                      plaintext password), hashed role/distribution/permission IDs, and user type
     * @return the persisted User populated with assigned roles, permissions, and distributions
     * @throws EmailAlreadyUsedException if the requested email is already in use
     * @throws InvalidIDException if any provided role, permission, or distribution ID is invalid,
     *                            or if distribution assignments exceed the admin's permitted distributions
     * @throws UserNotFoundException if the currently authenticated admin user cannot be found
     */
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

    /**
     * Retrieve a paginated list of users according to the provided paging and sorting criteria.
     *
     * @param pageable paging and sorting information for the query
     * @return a page of User entities matching the requested page and sort parameters
     */
    public Page<User> getUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    /**
     * Update an existing user's basic information, assigned roles, permissions, and distributions.
     *
     * The method resolves role, permission, and distribution IDs from the DTO (using hashed IDs),
     * validates existence and counts, ensures the user's membership in each distribution, persists
     * updated distributions, and saves the updated user entity.
     *
     * @param dto the update payload containing user fields and lists of hashed IDs for roles, permissions, and distributions
     * @param id  the numeric id of the user to update
     * @return the updated and persisted User
     * @throws UserNotFoundException      if no user exists with the given id
     * @throws EmailAlreadyUsedException  if the requested email is already taken by another user
     * @throws InvalidIDException         if any provided role, permission, or distribution id cannot be decoded or does not exist
     */
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
