package com.skynetauth.auth_service.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.skynetauth.auth_service.models.Distribution;

import com.skynetauth.auth_service.repositories.DistributionRepository;


@Service
public class DistributionService {
    private final DistributionRepository distributionRepository;

    /**
     * Create a DistributionService backed by the given DistributionRepository.
     *
     * @param distributionRepository the repository used to access Distribution entities
     */
    public DistributionService(DistributionRepository distributionRepository) {
        this.distributionRepository = distributionRepository;
    }

    /**
     * Retrieve all distributions representing roles with their permissions.
     *
     * @return a list of all Distribution entities
     */
    public List<Distribution> getAllRolesWithPermissions() {
        return distributionRepository.findAll();
    }
}
