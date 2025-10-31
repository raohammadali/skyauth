package com.skynetauth.auth_service.Controller;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.skynetauth.auth_service.Enum.CustomHttpStatus;
import com.skynetauth.auth_service.dto.request.ImpersonateRequest;
import com.skynetauth.auth_service.dto.request.LoginRequest;
import com.skynetauth.auth_service.dto.request.SignupRequest;
import com.skynetauth.auth_service.dto.request.UpdateRequest;
import com.skynetauth.auth_service.dto.response.ApiResponse;
import com.skynetauth.auth_service.dto.response.LoginResponse;
import com.skynetauth.auth_service.dto.response.UserResponse;
import com.skynetauth.auth_service.exceptions.InvalidIDException;
import com.skynetauth.auth_service.mapper.UserMapper;
import com.skynetauth.auth_service.models.User;
import com.skynetauth.auth_service.service.AuthenticationService;
import com.skynetauth.auth_service.service.CustomUserDetailsService;
import com.skynetauth.auth_service.utils.HashIdUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin
public class AuthenticationController extends BaseController {

    private final CustomUserDetailsService userService;

    private final AuthenticationService authService;

    private final SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();

    private final HashIdUtil hashIdUtil;

    /**
     * Creates an AuthenticationController and initializes its required dependencies.
     *
     * @param userService the user management service used to create, edit, and fetch users
     * @param authService the authentication service used for login and impersonation operations
     * @param hashIdUtil utility for encoding and decoding hashed identifiers
     */
    public AuthenticationController(CustomUserDetailsService userService,
            AuthenticationService authService, HashIdUtil hashIdUtil) {
        this.userService = userService;
        this.authService = authService;
        this.hashIdUtil = hashIdUtil;
    }

    /**
     * Authenticate a user and produce a standardized login response.
     *
     * @param loginRequest the credentials and user type for authentication
     * @return an ApiResponse containing the LoginResponse on successful authentication with HTTP 200 and status S_LOGIN
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@RequestBody @Valid LoginRequest loginRequest) {
        LoginResponse loginResponse = authService.authenticate(loginRequest.getEmail(),
                loginRequest.getPassword(), loginRequest.getUserType());
        return this.buildResponse(loginResponse,
                true,
                HttpStatus.OK, CustomHttpStatus.S_LOGIN);
    }

    /**
     * Create a new user from the provided signup request.
     *
     * @param signupRequest the signup details used to create the new user
     * @return a ResponseEntity containing an ApiResponse with the created UserResponse, HTTP 200 status, and CustomHttpStatus.S_SIGNUP
     */
    @PreAuthorize("hasAuthority('CAN_CREATE_USERS')")
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<UserResponse>> registerUser(@Valid @RequestBody SignupRequest signupRequest) {
        User user = userService.createUser(signupRequest);
        UserMapper userMapper = new UserMapper(hashIdUtil);
        return this.buildResponse(userMapper.toUserResponse(user), true,
                HttpStatus.OK, CustomHttpStatus.S_SIGNUP);
    }

    /**
     * Updates an existing user's data identified by a hashed id.
     *
     * @param dto the update payload with the user's new field values
     * @param id  the hashed identifier of the user to update
     * @return a ResponseEntity containing an ApiResponse with the updated UserResponse
     * @throws InvalidIDException if the provided `id` cannot be decoded to a valid user identifier
     */
    @PostMapping("/update/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> editUser(@RequestBody @Valid UpdateRequest dto,
            @PathVariable String id) {
        UserMapper userMapper = new UserMapper(hashIdUtil);
        try {
            Long decodedId = hashIdUtil.decodeId(id);
            return this.buildResponse(userMapper.toUserResponse(userService.editUser(dto, decodedId)), true,
                    HttpStatus.OK, CustomHttpStatus.S_UPDATE);
        } catch (IllegalArgumentException e) {
            throw new InvalidIDException("user");
        }
    }

    /**
     * Performs an impersonation login for the user identified by the provided email.
     *
     * @param impersonateRequest request containing the target user's email to impersonate
     * @param request HTTP servlet request for the current request context
     * @return an ApiResponse containing the `LoginResponse` when impersonation succeeds; on failure, an ApiResponse with `null` data and `success = false`
     */
    @PostMapping("/impersonate/login")
    @PreAuthorize("hasAuthority('IMPERSONATE')")
    public ResponseEntity<ApiResponse<LoginResponse>> impersonateLogin(
            @RequestBody @Valid ImpersonateRequest impersonateRequest,
            HttpServletRequest request) {
        try {
            LoginResponse loginResponse = authService.impersonate(impersonateRequest.getEmail());
            return this.buildResponse(loginResponse, true, HttpStatus.OK, CustomHttpStatus.S_LOGIN);
        } catch (Exception e) {
            return this.buildResponse(null, false, HttpStatus.BAD_REQUEST, CustomHttpStatus.SERVER_ERROR);
        }
    }

    /**
     * Retrieves a paginated and sorted page of users and returns it as UserResponse objects.
     *
     * @param page the zero-based page index to retrieve (default 0)
     * @param size the number of users per page (default 10)
     * @param sortBy the user field to sort by (default "firstName")
     * @param direction the sort direction, either "asc" or "desc" (default "asc")
     * @return a ResponseEntity containing an ApiResponse whose data is a Page of UserResponse for the requested page and sort order
     */
    @GetMapping("/all-users")
    public ResponseEntity<ApiResponse<Page<UserResponse>>> getAllUsers(@RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "firstName") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {
        Sort sort = direction.equals("asc") ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        PageRequest pageRequest = PageRequest.of(page, size, sort);
        Page<User> userPage = userService.getUsers(pageRequest);

        UserMapper userMapper = new UserMapper(hashIdUtil);
        Page<UserResponse> userResponsePage = userPage.map(userMapper::toUserResponse);

        return this.buildResponse(userResponsePage, true,
                HttpStatus.OK, CustomHttpStatus.S_FETCH_U);
    }

    /**
     * Performs logout for the current request and clears the security context.
     *
     * @return an ApiResponse containing an empty List<String> and a logout success status
     */
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<List<String>>> logout(HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication) {

        // dumy logout
        logoutHandler.logout(request, response, authentication);
        request.getSession().removeAttribute("SPRING_SECURITY_CONTEXT");
        request.getSession().invalidate();
        SecurityContextHolder.clearContext();
        return this.buildResponse(new ArrayList<>(), true, HttpStatus.OK, CustomHttpStatus.S_LOGOUT);

    }
}