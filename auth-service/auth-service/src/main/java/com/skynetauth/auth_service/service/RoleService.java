package com.skynetauth.auth_service.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.skynetauth.auth_service.Enum.UserType;
import com.skynetauth.auth_service.models.Role;
import com.skynetauth.auth_service.repositories.RoleRepository;

@Service
public class RoleService {
    private final RoleRepository roleRepository;

    /**
     * Creates a RoleService using the provided RoleRepository.
     *
     * @param roleRepository repository used to access and query Role entities
     */
    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    /**
     * Retrieve all roles along with their permissions.
     *
     * @return a list of Role entities with their associated permissions.
     */
    public List<Role> getAllRolesWithPermissions() {
        return roleRepository.findAll();
    }

    /**
     * Retrieve roles associated with the specified user type.
     *
     * @param userType the user type to filter roles by
     * @return a list of roles that match the given user type
     */
    public List<Role> getRolesOfUserType(UserType userType) {
        return roleRepository.findByUserType(userType);
    }
}
