package com.skynetauth.auth_service.mapper;

import org.springframework.stereotype.Component;

import com.skynetauth.auth_service.dto.dto.SoldToDTO;
import com.skynetauth.auth_service.models.SoldTo;
import com.skynetauth.auth_service.utils.HashIdUtil;

@Component
public class SoldToMapper {

    private final HashIdUtil hashIdUtil;
    private final ShipToMapper shipToMapper;
    private final DistributionMapper distributionMapper;

    public SoldToMapper(HashIdUtil hashIdUtil, ShipToMapper shipToMapper, DistributionMapper distributionMapper) {
        this.hashIdUtil = hashIdUtil;
        this.shipToMapper = shipToMapper;
        this.distributionMapper = distributionMapper;
    }

    public SoldToDTO toSoldToDTO(SoldTo soldTo) {
        SoldToDTO soldToDTO = new SoldToDTO();
        soldToDTO.setId(hashIdUtil.encodeId(soldTo.getId()));
        soldToDTO.setName(soldTo.getName());
        soldToDTO.setShipTos(soldTo.getShipTos().stream().map(shipToMapper::toShipToDTO).toList());
        soldToDTO.setDistribution(distributionMapper.toDistributionDto(soldTo.getDistributions()));
        return soldToDTO;

    }
}
