package com.skynetauth.auth_service;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;

import org.mockito.Mockito;

import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.skynetauth.auth_service.Controller.AuthenticationController;
import com.skynetauth.auth_service.Enum.UserType;
import com.skynetauth.auth_service.dto.dto.PermissionDto;
import com.skynetauth.auth_service.dto.response.LoginResponse;
import com.skynetauth.auth_service.mapper.UserMapper;

import com.skynetauth.auth_service.service.AuthenticationService;
import com.skynetauth.auth_service.service.CustomUserDetailsService;
import com.skynetauth.auth_service.utils.HashIdUtil;

import io.restassured.module.mockmvc.RestAssuredMockMvc;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@SpringBootTest
@TestPropertySource(properties = "spring.main.allow-bean-definition-overriding=true")
public abstract class BaseContractTest {

    @Autowired
    AuthenticationController authenticationController;

    @MockitoBean
    private CustomUserDetailsService userService;

    @MockitoBean 
    AuthenticationService authService;

    @MockitoBean 
    HashIdUtil hashIdUtil;

    @MockitoBean 
    UserMapper userMapper;
    @BeforeEach
    public void setup() {
        RestAssuredMockMvc.standaloneSetup(authenticationController);
        Mockito.when(authService.authenticate("admin@example.com", "securepassword", UserType.ADMIN)).thenReturn(new LoginResponse("mocked-jwt-token","admin@example.com", "mocked-jwt-token", List.of(new PermissionDto("12", "Read Permission", UserType.ADMIN))));
    }
}


