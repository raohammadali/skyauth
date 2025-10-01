package com.skynetauth.auth_service.mapper;

import org.springframework.stereotype.Component;

import com.skynetauth.auth_service.dto.response.UserResponse;
import com.skynetauth.auth_service.models.User;
import com.skynetauth.auth_service.utils.HashIdUtil;

import java.util.List;

@Component
public class UserMapper {

    private final HashIdUtil hashIdUtil;
    private final RoleMapper roleMapper;
    private final PermissionMapper permissionMapper;
    private final DistributionMapper distributionMapper;

    /**
     * Creates a UserMapper configured with the given HashIdUtil and initializes mappers for roles, permissions, and distributions.
     *
     * @param hashIdUtil utility used to encode entity IDs for DTOs
     */
    public UserMapper(
        HashIdUtil hashIdUtil
    ) {
        this.hashIdUtil = hashIdUtil;
        this.roleMapper = new RoleMapper(hashIdUtil);
        this.permissionMapper = new PermissionMapper(hashIdUtil);
        this.distributionMapper = new DistributionMapper(hashIdUtil);
    }

    /**
     * Maps a User domain model to a UserResponse DTO.
     *
     * @param user the domain User to convert; may be null
     * @return the mapped UserResponse, or null if {@code user} is null
     */
    public UserResponse toUserResponse(User user) {
        if (user == null) return null;

        UserResponse dto = new UserResponse();
        dto.setId(hashIdUtil.encodeId(user.getId()));
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setEmail(user.getEmail());
        dto.setPhone(user.getPhone());
        dto.setUserType(user.getUserType());
        dto.setRoles(roleMapper.toRoleDtos(user.getRoles()));
        dto.setPermissions(permissionMapper.toPermissionDtos(user.getPermissions()));
        dto.setDistributions(distributionMapper.toDistributionDtos(user.getDistributions()));

        return dto;
    }

    /**
     * Map a list of User domain objects to UserResponse DTOs.
     *
     * @param users the users to convert; individual null entries will be mapped to null
     * @return a list of UserResponse objects corresponding to the input users, preserving order
     */
    public List<UserResponse> toUserResponses(List<User> users) {
        return users.stream()
                .map(this::toUserResponse).toList();
    }
}
