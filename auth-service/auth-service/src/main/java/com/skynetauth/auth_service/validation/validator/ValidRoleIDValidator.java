package com.skynetauth.auth_service.validation.validator;

import java.util.List;

import org.springframework.stereotype.Component;

import com.skynetauth.auth_service.Enum.UserType;
import com.skynetauth.auth_service.dto.request.SignupRequest;
import com.skynetauth.auth_service.models.Role;
import com.skynetauth.auth_service.repositories.RoleRepository;
import com.skynetauth.auth_service.utils.HashIdUtil;
import com.skynetauth.auth_service.validation.annotation.ValidRoleID;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

@Component
public class ValidRoleIDValidator implements ConstraintValidator<ValidRoleID, SignupRequest>{

    private final HashIdUtil hashIdUtil;
    private final RoleRepository roleRepository;

    public ValidRoleIDValidator(HashIdUtil hashIdUtil, RoleRepository roleRepository) {
        this.hashIdUtil = hashIdUtil;
        this.roleRepository = roleRepository;
    }

    @Override
    public boolean isValid(SignupRequest request, ConstraintValidatorContext context) {
        try {
            List<String> value = request.getRoles();
            UserType userType = request.getUserType();
            List<Role> roles = roleRepository
                        .findByIdInAndUserType(value.stream().map(hashIdUtil::decodeId).toList(), userType);
            return roles.size() == value.size();
        } catch (Exception e) {
            return false;
        }
    }
    
}
