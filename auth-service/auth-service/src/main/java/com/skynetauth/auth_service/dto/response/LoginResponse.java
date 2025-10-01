package com.skynetauth.auth_service.dto.response;
import java.util.*;

import com.skynetauth.auth_service.dto.dto.PermissionDto;

import lombok.Data;

@Data
public class LoginResponse {
    private String token;
    private String email;
    private List<PermissionDto> permissions; 
}
