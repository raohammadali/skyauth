package com.skynetauth.auth_service.Controller;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.skynetauth.auth_service.Enum.CustomHttpStatus;
import com.skynetauth.auth_service.dto.response.ApiResponse;


public abstract class BaseController {

    protected <T> ResponseEntity<ApiResponse<T>> buildResponse(T data, boolean success, HttpStatus status, CustomHttpStatus customStatus) {
        ApiResponse<T> response = new ApiResponse<>(success, data, customStatus.value());
        return ResponseEntity.status(status).body(response);
    }
}


