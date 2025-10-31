package com.skynetauth.auth_service.Controller;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.skynetauth.auth_service.Enum.CustomHttpStatus;
import com.skynetauth.auth_service.dto.response.ApiResponse;


public abstract class BaseController {

    /**
     * Builds a standardized API response and wraps it in a ResponseEntity with the specified HTTP status.
     *
     * @param  data         the response payload
     * @param  success      whether the operation succeeded
     * @param  status       the HTTP status for the ResponseEntity
     * @param  customStatus an application-specific status to include in the ApiResponse
     * @return              a ResponseEntity containing an ApiResponse with the provided success flag, payload, and custom status
     */
    protected <T> ResponseEntity<ApiResponse<T>> buildResponse(T data, boolean success, HttpStatus status, CustomHttpStatus customStatus) {
        ApiResponse<T> response = new ApiResponse<>(success, data, customStatus.value());
        return ResponseEntity.status(status).body(response);
    }
}


