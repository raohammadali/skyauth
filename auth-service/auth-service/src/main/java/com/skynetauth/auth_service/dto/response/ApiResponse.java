package com.skynetauth.auth_service.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ApiResponse<T> {
    private boolean success;
    private T data;
    private int statusCode;

    /**
     * Create an ApiResponse with the specified success flag, payload, and status code.
     *
     * @param success    whether the operation was successful
     * @param data       the response payload
     * @param statusCode an HTTP-like status code representing the outcome
     */
    public ApiResponse(boolean success, T data, int statusCode) {
        this.success = success;
        this.data = data;
        this.statusCode = statusCode;
    }
}

