package com.skynetauth.auth_service.exceptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.skynetauth.auth_service.Controller.BaseController;
import com.skynetauth.auth_service.Enum.CustomHttpStatus;
import com.skynetauth.auth_service.dto.response.ApiResponse;


@ControllerAdvice
public class GlobalExceptionHandler extends BaseController {

    /**
     * Builds a BAD_REQUEST response that maps each invalid request field to its validation message.
     *
     * @param ex the MethodArgumentNotValidException containing binding/validation errors
     * @return a ResponseEntity containing an ApiResponse whose body is a Map from field names to error messages, with HTTP status 400 and custom status E_INVALID_INPUT
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors()
                .forEach(error -> errors.put(((FieldError) error).getField(), error.getDefaultMessage()));
        return this.buildResponse(errors, false,
                HttpStatus.BAD_REQUEST, CustomHttpStatus.E_INVALID_INPUT);
    }

    /**
     * Handles attempts to use an email address that is already registered.
     *
     * @param ex the exception indicating the email is already in use
     * @return an ApiResponse containing an empty list as the body, `success = false`, HTTP 409 Conflict, and custom status `EMAIL_ALR_USED`
     */
    @ExceptionHandler(EmailAlreadyUsedException.class)
    public ResponseEntity<ApiResponse<List<String>>> handleEmailAlreadyUsedException(EmailAlreadyUsedException ex) {
        return this.buildResponse(new ArrayList<>(), false, HttpStatus.CONFLICT,
                CustomHttpStatus.EMAIL_ALR_USED);
    }

    /**
     * Handles invalid email or password authentication attempts and maps them to a standardized API response.
     *
     * @return a 400 Bad Request response whose body is an ApiResponse containing an empty list and the `F_LOGIN` custom status
     */
    @ExceptionHandler(InvalidEmailPasswordException.class)
    public ResponseEntity<ApiResponse<List<String>>> handleInvalidEmailPasswordException(
            InvalidEmailPasswordException ex) {
        return this.buildResponse(new ArrayList<>(), false, HttpStatus.BAD_REQUEST,
                CustomHttpStatus.F_LOGIN);
    }

    /**
     * Handles cases where a requested user cannot be found.
     *
     * @param ex the exception indicating the user was not found
     * @return an HTTP 404 response containing an ApiResponse with an empty list body and the `E_U_NOT_FOUND` custom status
     */
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiResponse<List<String>>> handleUserNotFoundException(UserNotFoundException ex) {
        return this.buildResponse(new ArrayList<>(), false, HttpStatus.NOT_FOUND,
                CustomHttpStatus.E_U_NOT_FOUND);
    }

    /**
     * Handle an InvalidIDException by returning a BAD_REQUEST ApiResponse with a custom status
     * that identifies which entity's id was invalid.
     *
     * @param ex the exception whose message identifies the invalid id type; expected values are
     *           {@code "role"}, {@code "permission"}, {@code "user"}, or any other value for distribution
     * @return a ResponseEntity containing an ApiResponse with an empty list body, `success` set to false,
     *         HTTP status 400 (Bad Request), and a custom error status selected according to the exception message:
     *         {@code "role" -> E_INVALID_ROLE_ID}, {@code "permission" -> E_INVALID_PERMISSION_ID},
     *         {@code "user" -> E_INVALID_USER_ID}, otherwise {@code E_INVALID_DISTRIBUTION_ID}
     */
    @ExceptionHandler(InvalidIDException.class)
    public ResponseEntity<ApiResponse<List<String>>> handleInvalidIDException(InvalidIDException ex) {
        return this.buildResponse(new ArrayList<>(), false, HttpStatus.BAD_REQUEST,
                ex.getMessage().equals("role") ? CustomHttpStatus.E_INVALID_ROLE_ID
                        : ex.getMessage().equals("permission") ? CustomHttpStatus.E_INVALID_PERMISSION_ID
                                : ex.getMessage().equals("user") ? CustomHttpStatus.E_INVALID_USER_ID : CustomHttpStatus.E_INVALID_DISTRIBUTION_ID);
    }

    /**
     * Handle UnauthorizedException and produce an HTTP 401 response with the unauthorized custom status.
     *
     * @return ResponseEntity containing an ApiResponse whose body is an empty list, indicating failure with custom status E_UNAUTHORIZED and HTTP status 401.
     */
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ApiResponse<List<String>>> handleUnauthorizedException(UnauthorizedException ex) {
        return this.buildResponse(new ArrayList<>(), false, HttpStatus.UNAUTHORIZED, CustomHttpStatus.E_UNAUTHORIZED);
    }

    /**
     * Handle InvalidJWTTokenException by producing a standardized bad-request API response indicating an invalid token.
     *
     * @return a ResponseEntity containing an ApiResponse with an empty list body and the `E_INVALID_TOKEN` custom status (HTTP 400 Bad Request)
     */
    @ExceptionHandler(InvalidJWTTokenException.class)
    public ResponseEntity<ApiResponse<List<String>>> handleInvalidJWTTokenException(InvalidJWTTokenException ex) {
        return this.buildResponse(new ArrayList<>(), false, HttpStatus.BAD_REQUEST, CustomHttpStatus.E_INVALID_TOKEN);
    }

    /**
     * Handle cases where a requested username cannot be found during authentication.
     *
     * @param ex the thrown UsernameNotFoundException
     * @return a ResponseEntity containing an ApiResponse with an empty list body and the `E_USERNAME_NOT_FOUND` custom status, returned with HTTP 404 Not Found
     */
    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ApiResponse<List<String>>> handleUsernameNotFoundException(UsernameNotFoundException ex) {
        return this.buildResponse(new ArrayList<>(), false, HttpStatus.NOT_FOUND,
                CustomHttpStatus.E_USERNAME_NOT_FOUND);
    }

    /**
     * Handles requests with unreadable or malformed HTTP message bodies.
     *
     * @return a ResponseEntity containing an ApiResponse whose body is an empty list, indicates failure, is mapped to HTTP 400 (Bad Request), and uses the `E_INVALID_INPUT` custom status.
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<List<String>>> handleHttpMessageNotReadableException(
            HttpMessageNotReadableException ex) {
        return this.buildResponse(new ArrayList<>(), false, HttpStatus.BAD_REQUEST, CustomHttpStatus.E_INVALID_INPUT);
    }

    /**
     * Handle Spring Security authorization denial and map it to a standardized API error response.
     *
     * @param ex the thrown AuthorizationDeniedException indicating the current request is not permitted
     * @return a ResponseEntity containing an ApiResponse with an empty list body, `success = false`, HTTP status 400 (Bad Request), and custom status `E_UNAUTHORIZED`
     */
    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<ApiResponse<List<String>>> handleAuthorizationDeniedException(
            AuthorizationDeniedException ex) {
        return this.buildResponse(new ArrayList<>(), false, HttpStatus.BAD_REQUEST, CustomHttpStatus.E_UNAUTHORIZED);
    }
}
