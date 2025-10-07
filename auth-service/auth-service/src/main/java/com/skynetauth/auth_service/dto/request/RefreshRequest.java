package com.skynetauth.auth_service.dto.request;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RefreshRequest {
    @NotBlank(message = "Refresh Token cannot be blank")
    private String refreshToken;
}