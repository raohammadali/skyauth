package com.skynetauth.auth_service.dto.request;

import java.util.List;
import java.util.Set;

import com.skynetauth.auth_service.Enum.UserType;
import com.skynetauth.auth_service.validation.annotation.ValidDistributionID;
import com.skynetauth.auth_service.validation.annotation.ValidPermissionID;
import com.skynetauth.auth_service.validation.annotation.ValidRoleID;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@ValidRoleID
public class UpdateRequest {
    @NotBlank(message = "First name cannot be null")
    @Pattern(regexp = "[a-zA-Z]{1,747}", message = "First name should only be characters, fitting in the 1-747 range")
    private String firstName;

    @NotBlank(message = "Last name cannot be null")
    @Pattern(regexp = "[a-zA-Z]{1,747}", message = "Last name should only be characters, fitting in the 1-747 range")
    private String lastName;

    @NotBlank(message = "Phone number cannot be null")
    @Pattern(regexp = "^\\+[\\d]+$", message = "Phone number can only consist of digits")
    private String phone;

    private String profileImageUrl;

    @NotNull(message = "User type cannot be null")
    private UserType userType;

    @NotBlank(message = "Email cannot be null")
    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")
    private String email;

    @NotEmpty(message = "Roles cannot be null")
    @Size(min = 1, message = "At least one role is required")

    private List<String> roles;

    @ValidPermissionID
    private List<String> permissions;

    @NotEmpty(message = "Sold Tos cannot be null")
    @Size(min = 1, message = "At least one Sold To is required")
    @Valid
    private Set<String> sold_tos;

    @NotEmpty(message = "Distributions cannot be null")
    @Size(min = 1, message = "At least one Distribution is required")
    @Valid
    @ValidDistributionID
    private List<String> distributions;

}
