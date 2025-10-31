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

    /**
     * Creates a DistributionController with the required service and mapper.
     *
     * @param distributionService service used to retrieve distribution data and associated permissions
     * @param distributionMapper  mapper used to convert Distribution entities to DistributionDto objects
     */
    public DistributionController(DistributionService distributionService, DistributionMapper distributionMapper) {
        this.distributionService = distributionService;
        this.distributionMapper = distributionMapper;
    }

    /**
     * Fetches all distributions including their permissions and returns them wrapped in a standardized API response.
     *
     * On success, the response contains the list of DistributionDto, HTTP 200 (OK) and CustomHttpStatus.S_DIST.
     * On failure, the response contains null data, indicates failure, HTTP 500 (Internal Server Error) and CustomHttpStatus.E_UNAUTHORIZED.
     *
     * @return ResponseEntity containing an ApiResponse whose data is the list of DistributionDto on success; on failure the ApiResponse contains null data and indicates an error.
     */
    @GetMapping("/distribution")
    public ResponseEntity<ApiResponse<List<DistributionDto>>> getAllDistribution() {
        try {
            List<Distribution> distributions = distributionService.getAllRolesWithPermissions();
            List<DistributionDto> distDTOs = distributionMapper.toDistributionDtos(distributions);
            return this.buildResponse(distDTOs, true, HttpStatus.OK, CustomHttpStatus.S_DIST);
        } catch (Exception e) {
            return this.buildResponse(null, false, HttpStatus.INTERNAL_SERVER_ERROR, CustomHttpStatus.E_UNAUTHORIZED);
        }
    }

}
