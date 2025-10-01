package com.skynetauth.auth_service.dto.dto;

import com.skynetauth.auth_service.Enum.UserType;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PermissionDto {
    private String id;
    private String name;
    private UserType userType;
}
