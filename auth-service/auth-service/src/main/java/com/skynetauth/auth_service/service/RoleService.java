package com.skynetauth.auth_service.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.skynetauth.auth_service.Enum.UserType;
import com.skynetauth.auth_service.models.Role;
import com.skynetauth.auth_service.repositories.RoleRepository;

@Service
public class RoleService {
    private final RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }


     @Transactional(readOnly = true)
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }
    @Transactional(readOnly = true)
    public List<Role> getRolesOfUserType(UserType userType) {
        return roleRepository.findByUserType(userType);
    }
}
