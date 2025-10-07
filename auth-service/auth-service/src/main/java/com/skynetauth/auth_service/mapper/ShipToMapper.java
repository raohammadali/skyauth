package com.skynetauth.auth_service.mapper;

import org.springframework.stereotype.Component;

import com.skynetauth.auth_service.dto.dto.ShipToDTO;
import com.skynetauth.auth_service.models.ShipTo;
import com.skynetauth.auth_service.utils.HashIdUtil;

@Component
public class ShipToMapper {

    private final HashIdUtil hashIdUtil;

    public ShipToMapper(HashIdUtil hashIdUtil) {
        this.hashIdUtil = hashIdUtil;
    }

    public ShipToDTO toShipToDTO(ShipTo shipTo) {
        ShipToDTO shipToDTO = new ShipToDTO();
        shipToDTO.setId(hashIdUtil.encodeId(shipTo.getId()));
        shipToDTO.setName(shipTo.getName());
        return shipToDTO;
    }
}
