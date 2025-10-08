package com.skynetauth.auth_service.mapper;

import org.springframework.stereotype.Component;

import com.skynetauth.auth_service.dto.dto.RoleDto;
import com.skynetauth.auth_service.models.Role;
import com.skynetauth.auth_service.utils.HashIdUtil;

import java.util.List;

@Component
public class RoleMapper {

    private final HashIdUtil hashIdUtil;

    public RoleMapper(HashIdUtil hashIdUtil) {
        this.hashIdUtil = hashIdUtil;
    }

    public RoleDto toRoleDto(Role role) {
        if (role == null) return null;
        PermissionMapper permissionMapper = new PermissionMapper(hashIdUtil);
        return new RoleDto(hashIdUtil.encodeId(role.getId()), role.getName(), permissionMapper.toPermissionDtos(role.getPermissions()), role.getUserType());
    }

    public List<RoleDto> toRoleDtos(List<Role> roles) {
        return roles.stream()
                .map(this::toRoleDto).toList();
    }
}

