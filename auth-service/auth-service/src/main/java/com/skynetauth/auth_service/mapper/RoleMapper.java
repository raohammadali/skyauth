package com.skynetauth.auth_service.mapper;

import org.springframework.stereotype.Component;

import com.skynetauth.auth_service.dto.dto.RoleDto;
import com.skynetauth.auth_service.models.Role;
import com.skynetauth.auth_service.utils.HashIdUtil;

import java.util.List;

@Component
public class RoleMapper {

    private final HashIdUtil hashIdUtil;

    /**
     * Constructs a RoleMapper that uses the given HashIdUtil to encode role identifiers.
     *
     * @param hashIdUtil utility for encoding numeric IDs into hashed string representations
     */
    public RoleMapper(HashIdUtil hashIdUtil) {
        this.hashIdUtil = hashIdUtil;
    }

    /**
     * Map a Role domain object to a RoleDto.
     *
     * @param role the source Role to convert
     * @return the mapped RoleDto with an encoded id, name, permissions, and userType; returns `null` if {@code role} is {@code null}
     */
    public RoleDto toRoleDto(Role role) {
        if (role == null) return null;
        PermissionMapper permissionMapper = new PermissionMapper(hashIdUtil);
        return new RoleDto(hashIdUtil.encodeId(role.getId()), role.getName(), permissionMapper.toPermissionDtos(role.getPermissions()), role.getUserType());
    }

    /**
     * Converts a list of Role objects to a list of RoleDto objects preserving input order.
     *
     * @param roles the list of Role objects to convert; elements may be null (resulting entries will be null)
     * @return a list of RoleDto objects corresponding to the input list, in the same order
     * @throws NullPointerException if {@code roles} is null
     */
    public List<RoleDto> toRoleDtos(List<Role> roles) {
        return roles.stream()
                .map(this::toRoleDto).toList();
    }
}

