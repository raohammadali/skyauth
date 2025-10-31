package com.skynetauth.auth_service.dto.request;

import java.util.List;

import com.skynetauth.auth_service.Enum.UserType;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SignupRequest {
    @NotBlank(message = "First name cannot be null")
    @Pattern(regexp = "[a-zA-Z]{1,747}", message = "First name should only be characters, fitting in the 1-747 range")
    private String firstName;

    @NotBlank(message = "Last name cannot be null")
    @Pattern(regexp = "[a-zA-Z]{1,747}", message = "Last name should only be characters, fitting in the 1-747 range")
    private String lastName;

    @NotBlank(message = "Email cannot be null")
    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")
    private String email;

    @NotNull(message = "User type cannot be null")
    private UserType userType;

    @NotEmpty(message = "Distributions cannot be null")
    @Size(min = 1, message = "At least one Distribution is required")
    @Valid
    private List<String> distributions;

    @NotBlank(message = "Phone number cannot be null")
    @Pattern(regexp = "^\\+[\\d]+$", message = "Phone number can only consist of digits")
    private String phone;

    @NotBlank(message = "Password cannot be null")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z0-9]).{8,}$", message = "Password must be at least 8 characters long and include at least one uppercase letter, one lowercase letter, one digit, and one special character.")
    private String password;

    @NotEmpty(message = "Roles cannot be null")
    @Size(min = 1, message = "At least one role is required")
    @Valid
    private List<String> roles;

    private List<String> permissions;

}
