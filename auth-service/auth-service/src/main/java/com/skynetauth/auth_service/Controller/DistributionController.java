package com.skynetauth.auth_service.Controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.skynetauth.auth_service.Enum.CustomHttpStatus;
import com.skynetauth.auth_service.dto.dto.DistributionDto;
import com.skynetauth.auth_service.dto.response.ApiResponse;
import com.skynetauth.auth_service.mapper.DistributionMapper;
import com.skynetauth.auth_service.models.Distribution;
import com.skynetauth.auth_service.service.DistributionService;

@RestController()
@RequestMapping("/api")
@CrossOrigin
public class DistributionController extends BaseController {
    private final DistributionService distributionService;
    private final DistributionMapper distributionMapper;

    public DistributionController(DistributionService distributionService, DistributionMapper distributionMapper) {
        this.distributionService = distributionService;
        this.distributionMapper = distributionMapper;
    }

    @GetMapping("/distribution")
    public ResponseEntity<ApiResponse<List<DistributionDto>>> getAllDistribution() {
        try {
            List<Distribution> distributions = distributionService.getAllDistributions();
            List<DistributionDto> distDTOs = distributionMapper.toDistributionDtos(distributions);
            return this.buildResponse(distDTOs, true, HttpStatus.OK, CustomHttpStatus.S_DIST);
        } catch (Exception e) {
            return this.buildResponse(null, false, HttpStatus.INTERNAL_SERVER_ERROR, CustomHttpStatus.SERVER_ERROR);
        }
    }

}
