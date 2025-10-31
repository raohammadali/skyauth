package com.skynetauth.auth_service.mapper;

import org.springframework.stereotype.Component;

import com.skynetauth.auth_service.dto.dto.PermissionDto;
import com.skynetauth.auth_service.models.Permission;
import com.skynetauth.auth_service.utils.HashIdUtil;

import java.util.List;

@Component
public class PermissionMapper {

    private final HashIdUtil hashIdUtil;

    /**
     * Creates a PermissionMapper that uses the given HashIdUtil to encode Permission IDs.
     *
     * @param hashIdUtil utility used to encode numeric IDs to hashed string representations
     */
    public PermissionMapper(HashIdUtil hashIdUtil) {
        this.hashIdUtil = hashIdUtil;
    }

    /**
     * Convert a Permission entity to a PermissionDto.
     *
     * @param permission the Permission entity to convert; may be null
     * @return a PermissionDto containing the permission's encoded id, name, and user type, or `null` if {@code permission} is null
     */
    public PermissionDto toPermissionDto(Permission permission) {
        if (permission == null) return null;
        return new PermissionDto(hashIdUtil.encodeId(permission.getId()), permission.getName(), permission.getUserType());
    }

    /**
     * Converts a list of Permission entities into a list of PermissionDto objects.
     *
     * @param permissions the list of Permission entities to convert
     * @return a list of PermissionDto objects corresponding to the input list; empty if the input list is empty
     */
    public List<PermissionDto> toPermissionDtos(List<Permission> permissions) {
        return permissions.stream()
                .map(this::toPermissionDto)
                .toList();
    }
}
