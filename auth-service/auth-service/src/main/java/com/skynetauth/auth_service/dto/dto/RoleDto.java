package com.skynetauth.auth_service.dto.dto;

import java.util.List;

import com.skynetauth.auth_service.Enum.UserType;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RoleDto {
    private String id;
    private String name;
    private List<PermissionDto> permissions;
    private UserType userType;
}
