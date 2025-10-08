package com.skynetauth.auth_service.dto.response;
import java.util.*;

import com.skynetauth.auth_service.dto.dto.PermissionDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponse {
    private String token;
    private String email;
    private String refreshToken;
    private List<PermissionDto> permissions; 
}
