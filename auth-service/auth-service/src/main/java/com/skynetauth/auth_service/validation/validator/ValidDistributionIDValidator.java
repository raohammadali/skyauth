package com.skynetauth.auth_service.validation.validator;

import java.util.List;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.skynetauth.auth_service.models.Distribution;
import com.skynetauth.auth_service.models.User;
import com.skynetauth.auth_service.repositories.DistributionRepository;
import com.skynetauth.auth_service.repositories.UserRepository;
import com.skynetauth.auth_service.utils.HashIdUtil;
import com.skynetauth.auth_service.validation.annotation.ValidDistributionID;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

@Component
public class ValidDistributionIDValidator implements ConstraintValidator<ValidDistributionID, List<String>>{

    private final HashIdUtil hashIdUtil;
    private final DistributionRepository distributionRepository;
    private final UserRepository userRepository;

    public ValidDistributionIDValidator(HashIdUtil hashIdUtil, DistributionRepository distributionRepository, UserRepository userRepository) {
        this.hashIdUtil = hashIdUtil;
        this.distributionRepository = distributionRepository;
        this.userRepository = userRepository;
    }

    @Override
    public boolean isValid(List<String> value, ConstraintValidatorContext context) {
        String email = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        List<String> adminDistributions = userRepository.findByEmailIgnoreCase(email).orElse(new User()).getDistributions().stream().map(d -> d.getName()).toList();
        
        try {
            List<Distribution> distributions = distributionRepository
                        .findAllById(value.stream().map(hashIdUtil::decodeId).toList());
            for (String dist : distributions.stream().map(d -> d.getName()).toList()) {
                if (!adminDistributions.contains(dist)) {
                    return false;
                }
            }
            return distributions.size() == value.size();
        } catch (Exception e) {
            return false;
        }
    }
    
}
