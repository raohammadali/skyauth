package com.skynetauth.auth_service.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ApiResponse<T> {
    private boolean success;
    private T data;
    private int statusCode;

    public ApiResponse(boolean success, T data, int statusCode) {
        this.success = success;
        this.data = data;
        this.statusCode = statusCode;
    }
}

