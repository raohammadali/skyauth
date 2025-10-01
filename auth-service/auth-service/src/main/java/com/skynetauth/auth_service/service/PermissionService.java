package com.skynetauth.auth_service.service;
import java.util.List;
import org.springframework.stereotype.Service;

import com.skynetauth.auth_service.Enum.UserType;
import com.skynetauth.auth_service.models.Permission;
import com.skynetauth.auth_service.repositories.PermissionRepository;

@Service
public class PermissionService {
    private final PermissionRepository permissionRepository;

    /**
     * Creates a PermissionService backed by the provided PermissionRepository.
     */
    public PermissionService(PermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }

    /**
     * Retrieve all Permission records.
     *
     * @return a list of all Permission entities, or an empty list if none are found.
     */
    public List<Permission> getAllPermissions() {
        return permissionRepository.findAll();
    }

    /**
     * Retrieve permissions associated with the specified user type.
     *
     * @param userType the user type whose permissions should be returned
     * @return a list of Permission objects for the given user type
     */
    public List<Permission> getPermissionsByUserType(UserType userType) {
        return permissionRepository.findByUserType(userType);
    }
}
