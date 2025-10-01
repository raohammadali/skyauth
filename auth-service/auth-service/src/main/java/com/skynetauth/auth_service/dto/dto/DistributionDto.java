package com.skynetauth.auth_service.dto.dto;

import lombok.Data;

@Data
public class DistributionDto {
    private String id;
    private String name;

    /**
     * Create a DistributionDto with the specified identifier and name.
     *
     * @param id   the distribution identifier
     * @param name the distribution display name
     */
    public DistributionDto(String id, String name) {
        this.id = id;
        this.name = name;
    }
}

