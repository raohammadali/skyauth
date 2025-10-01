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

    public AuthenticationController(CustomUserDetailsService userService,
            AuthenticationService authService, HashIdUtil hashIdUtil) {
        this.userService = userService;
        this.authService = authService;
        this.hashIdUtil = hashIdUtil;
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@RequestBody @Valid LoginRequest loginRequest) {
        LoginResponse loginResponse = authService.authenticate(loginRequest.getEmail(),
                loginRequest.getPassword(), loginRequest.getUserType());
        return this.buildResponse(loginResponse,
                true,
                HttpStatus.OK, CustomHttpStatus.S_LOGIN);
    }

    @PreAuthorize("hasAuthority('CAN_CREATE_USERS')")
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<UserResponse>> registerUser(@Valid @RequestBody SignupRequest signupRequest) {
        User user = userService.createUser(signupRequest);
        UserMapper userMapper = new UserMapper(hashIdUtil);
        return this.buildResponse(userMapper.toUserResponse(user), true,
                HttpStatus.OK, CustomHttpStatus.S_SIGNUP);
    }

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