package com.skynetauth.auth_service.mapper;

import org.springframework.stereotype.Component;

import com.skynetauth.auth_service.dto.dto.PermissionDto;
import com.skynetauth.auth_service.models.Permission;
import com.skynetauth.auth_service.utils.HashIdUtil;

import java.util.List;

@Component
public class PermissionMapper {

    private final HashIdUtil hashIdUtil;

    public PermissionMapper(HashIdUtil hashIdUtil) {
        this.hashIdUtil = hashIdUtil;
    }

    public PermissionDto toPermissionDto(Permission permission) {
        if (permission == null) return null;
        return new PermissionDto(hashIdUtil.encodeId(permission.getId()), permission.getName(), permission.getUserType());
    }

    public List<PermissionDto> toPermissionDtos(List<Permission> permissions) {
        return permissions.stream()
                .map(this::toPermissionDto)
                .toList();
    }
}
