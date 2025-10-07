package com.skynetauth.auth_service.mapper;

import org.springframework.stereotype.Component;

import com.skynetauth.auth_service.dto.dto.UserEventDto;
import com.skynetauth.auth_service.models.User;

@Component
public class UserEventMapper {
    public UserEventDto toDto(User user) {
        UserEventDto dto = new UserEventDto();
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setPhone(user.getPhone());
        return dto;
    }
}