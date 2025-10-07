package com.skynetauth.auth_service.validation.validator;

import java.util.List;

import org.springframework.stereotype.Component;

import com.skynetauth.auth_service.models.Permission;
import com.skynetauth.auth_service.repositories.PermissionRepository;
import com.skynetauth.auth_service.utils.HashIdUtil;
import com.skynetauth.auth_service.validation.annotation.ValidPermissionID;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

@Component
public class ValidPermissionIDValidator implements ConstraintValidator<ValidPermissionID, List<String>>{

    private final HashIdUtil hashIdUtil;
    private final PermissionRepository permissionRepository;

    public ValidPermissionIDValidator(HashIdUtil hashIdUtil, PermissionRepository permissionRepository) {
        this.hashIdUtil = hashIdUtil;
        this.permissionRepository = permissionRepository;
    }

    @Override
    public boolean isValid(List<String> value, ConstraintValidatorContext context) {
        try {
            List<Permission> permissions = permissionRepository
                        .findAllById(value.stream().map(hashIdUtil::decodeId).toList());
            return permissions.size() == value.size();
        } catch (Exception e) {
            return false;
        }
    }
    
}
