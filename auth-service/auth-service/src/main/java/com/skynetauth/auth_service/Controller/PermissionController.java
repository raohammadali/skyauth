package com.skynetauth.auth_service.Controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.skynetauth.auth_service.Enum.CustomHttpStatus;
import com.skynetauth.auth_service.Enum.UserType;
import com.skynetauth.auth_service.dto.dto.PermissionDto;
import com.skynetauth.auth_service.dto.response.ApiResponse;
import com.skynetauth.auth_service.mapper.PermissionMapper;
import com.skynetauth.auth_service.models.Permission;
import com.skynetauth.auth_service.service.PermissionService;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class PermissionController extends BaseController {
    private final PermissionService permissionService;
    private final PermissionMapper permissionMapper;

    /**
     * Create a PermissionController with the required service and mapper dependencies.
     */
    public PermissionController(PermissionService permissionService, PermissionMapper permissionMapper) {
        this.permissionService = permissionService;
        this.permissionMapper = permissionMapper;
    }
    
    // @GetMapping("/permissions")
    // public ResponseEntity<ApiResponse<List<PermissionDto>>> getAllPermissions() {
    //     try {
    //         List<Permission> permissions = permissionService.getAllPermissions();
    //         List<PermissionDto> permissionDTOs = permissionMapper.toPermissionDtos(permissions);
    //         return this.buildResponse(permissionDTOs, true, HttpStatus.OK, CustomHttpStatus.S_DIST);
    //     } catch (Exception e) {
    //         return this.buildResponse(null, false, HttpStatus.INTERNAL_SERVER_ERROR, CustomHttpStatus.E_UNAUTHORIZED);
    //     }
    /**
     * Retrieve permissions available for the specified user type.
     *
     * @param userType the user type whose permissions should be returned
     * @return a ResponseEntity containing an ApiResponse with a list of PermissionDto when successful;
     *         on failure, an ApiResponse with `null` data, `success = false`, HTTP 500 (INTERNAL_SERVER_ERROR),
     *         and CustomHttpStatus.E_UNAUTHORIZED
     */
    
    @GetMapping("/permissions")
    public ResponseEntity<ApiResponse<List<PermissionDto>>> getAllPermissions(@RequestParam UserType userType) {
        try {
            List<Permission> permissions = permissionService.getPermissionsByUserType(userType);
            return this.buildResponse(permissionMapper.toPermissionDtos(permissions), true, HttpStatus.OK, CustomHttpStatus.S_DIST);
        } catch (Exception e) {
            return this.buildResponse(null, false, HttpStatus.INTERNAL_SERVER_ERROR, CustomHttpStatus.E_UNAUTHORIZED);
        }
    }
}
