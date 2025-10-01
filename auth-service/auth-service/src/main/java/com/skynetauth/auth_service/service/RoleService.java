package com.skynetauth.auth_service.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.skynetauth.auth_service.Enum.UserType;
import com.skynetauth.auth_service.models.Role;
import com.skynetauth.auth_service.repositories.RoleRepository;

@Service
public class RoleService {
    private final RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public List<Role> getAllRolesWithPermissions() {
        return roleRepository.findAll();
    }

    public List<Role> getRolesOfUserType(UserType userType) {
        return roleRepository.findByUserType(userType);
    }
}
