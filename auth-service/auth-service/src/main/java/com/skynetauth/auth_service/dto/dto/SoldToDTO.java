package com.skynetauth.auth_service.dto.dto;

import java.util.List;

import lombok.Data;

@Data
public class SoldToDTO {
    private String id;
    private String name;
    List<ShipToDTO> shipTos;
    DistributionDto distribution;
}
