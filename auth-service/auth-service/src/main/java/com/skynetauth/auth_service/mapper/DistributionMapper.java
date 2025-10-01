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

    /**
     * Create a DistributionMapper that uses the given HashIdUtil to encode entity IDs.
     *
     * @param hashIdUtil utility used to encode Distribution entity IDs into external identifiers
     */
    public DistributionMapper(HashIdUtil hashIdUtil) {
        this.hashIdUtil = hashIdUtil;
    }

    /**
     * Map a Distribution entity to a DistributionDto.
     *
     * @param distribution the Distribution to map; may be null
     * @return the resulting DistributionDto with the distribution's id encoded and its name copied, or `null` if {@code distribution} is null
     */
    public DistributionDto toDistributionDto(Distribution distribution) {
        if (distribution == null) return null;
        return new DistributionDto(hashIdUtil.encodeId(distribution.getId()), distribution.getName());
    }

    /**
     * Convert a list of Distribution entities to a list of DistributionDto objects.
     *
     * @param distributions the source list whose elements will be converted; elements are mapped in order
     * @return a list of mapped DistributionDto objects whose elements correspond positionally to the input list (an empty list if the input is empty); if an input element is `null` its corresponding output element will be `null`
     */
    public List<DistributionDto> toDistributionDtos(List<Distribution> distributions) {
        return distributions.stream()
                .map(this::toDistributionDto)
                .collect(Collectors.toList());
    }
}

