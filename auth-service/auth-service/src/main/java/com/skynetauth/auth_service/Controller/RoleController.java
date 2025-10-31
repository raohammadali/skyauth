package com.skynetauth.auth_service.Controller;

import org.springframework.web.bind.annotation.RestController;

import com.skynetauth.auth_service.Enum.CustomHttpStatus;
import com.skynetauth.auth_service.Enum.UserType;
import com.skynetauth.auth_service.dto.dto.RoleDto;
import com.skynetauth.auth_service.dto.response.ApiResponse;
import com.skynetauth.auth_service.mapper.RoleMapper;
import com.skynetauth.auth_service.models.Role;
import com.skynetauth.auth_service.service.RoleService;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController()
@RequestMapping("/api")
@CrossOrigin
public class RoleController extends BaseController {
    private final RoleService roleService;
    private final RoleMapper roleMapper;

    /**
     * Create a RoleController with the specified service and mapper.
     *
     * @param roleService service used to retrieve Role data
     * @param roleMapper  mapper used to convert Role entities to RoleDto instances
     */
    public RoleController(RoleService roleService, RoleMapper roleMapper) {
        this.roleService = roleService;
        this.roleMapper = roleMapper;
    }

    // @GetMapping("/roles")
    // public ResponseEntity<ApiResponse<List<RoleDto>>> getAllRoles() {
    //     try {
    //         List<Role> roles = roleService.getAllRolesWithPermissions();
    //         List<RoleDto> roleDTOs = roleMapper.toRoleDtos(roles);
    //         return this.buildResponse(roleDTOs, true, HttpStatus.OK, CustomHttpStatus.S_ROLE);
    //     } catch (Exception e) {
    //         return this.buildResponse(null, false, HttpStatus.INTERNAL_SERVER_ERROR, CustomHttpStatus.E_UNAUTHORIZED);
    //     }
    /**
     * Retrieve the roles available for the specified user type.
     *
     * @param userType the user type to filter roles by
     * @return a ResponseEntity whose body is an ApiResponse containing the list of RoleDto for the specified user type; response uses HTTP 200 (OK) and custom status S_ROLE
     */

    @GetMapping("/roles")
    public ResponseEntity<ApiResponse<List<RoleDto>>> getRolesByUserType(@RequestParam UserType userType) {
        List<Role> roles = roleService.getRolesOfUserType(userType);
        return this.buildResponse(roleMapper.toRoleDtos(roles), true, HttpStatus.OK, CustomHttpStatus.S_ROLE);
    }

}
