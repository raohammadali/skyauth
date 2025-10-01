package com.skynetauth.auth_service.mapper;
import org.springframework.stereotype.Component;

import com.skynetauth.auth_service.dto.dto.DistributionDto;
import com.skynetauth.auth_service.models.Distribution;
import com.skynetauth.auth_service.utils.HashIdUtil;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class DistributionMapper {

    private final HashIdUtil hashIdUtil;

    public DistributionMapper(HashIdUtil hashIdUtil) {
        this.hashIdUtil = hashIdUtil;
    }

    public DistributionDto toDistributionDto(Distribution distribution) {
        if (distribution == null) return null;
        return new DistributionDto(hashIdUtil.encodeId(distribution.getId()), distribution.getName());
    }

    public List<DistributionDto> toDistributionDtos(List<Distribution> distributions) {
        return distributions.stream()
                .map(this::toDistributionDto)
                .collect(Collectors.toList());
    }
}

