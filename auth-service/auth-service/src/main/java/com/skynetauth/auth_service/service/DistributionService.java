package com.skynetauth.auth_service.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.skynetauth.auth_service.models.Distribution;

import com.skynetauth.auth_service.repositories.DistributionRepository;


@Service
public class DistributionService {
    private final DistributionRepository distributionRepository;

    public DistributionService(DistributionRepository distributionRepository) {
        this.distributionRepository = distributionRepository;
    }

    public List<Distribution> getAllRolesWithPermissions() {
        return distributionRepository.findAll();
    }
}
