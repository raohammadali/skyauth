package com.skynetauth.auth_service.dto.response;
import java.util.List;

import com.skynetauth.auth_service.Enum.UserType;
import com.skynetauth.auth_service.dto.dto.DistributionDto;
import com.skynetauth.auth_service.dto.dto.PermissionDto;
import com.skynetauth.auth_service.dto.dto.RoleDto;

import lombok.Data;
@Data
public class UserResponse {
    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private UserType userType;
    private List<RoleDto> roles;
    private List<DistributionDto> distributions;
    private List<PermissionDto> permissions;

}

