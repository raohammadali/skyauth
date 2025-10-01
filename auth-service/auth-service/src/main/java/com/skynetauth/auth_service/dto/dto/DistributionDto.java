package com.skynetauth.auth_service.dto.dto;

import lombok.Data;

@Data
public class DistributionDto {
    private String id;
    private String name;

    public DistributionDto(String id, String name) {
        this.id = id;
        this.name = name;
    }
}

