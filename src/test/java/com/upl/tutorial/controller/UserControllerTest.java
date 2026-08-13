package com.upl.tutorial.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import org.springframework.http.MediaType;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.upl.tutorial.dto.LoginRequest;
import com.upl.tutorial.dto.LoginResponse;
import com.upl.tutorial.dto.UserRequest;
import com.upl.tutorial.dto.UserResponse;
import com.upl.tutorial.service.AuthService;
import com.upl.tutorial.service.UserService;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private UserService userService;

    @Mock
    private AuthService authService;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setUp() {
        // standaloneSetup bypasses Spring context initialization while keeping @Valid
        // annotation processing active
        mockMvc = MockMvcBuilders.standaloneSetup(userController)
                .setValidator(new LocalValidatorFactoryBean())
                .build();
    }

    // ==========================================
    // 1. Tests for /instructors/register
    // ==========================================

    @Test
    void register_Success_ReturnsStatus201Created() throws Exception {
        // Given
        UserRequest request = new UserRequest();
        request.setEmail("john.doe@example.com");
        request.setPassword("Password123!");
        request.setName("John Doe");

        UserResponse mockResponse = new UserResponse();
        mockResponse.setUserId(1);
        mockResponse.setRole("instructor");

        when(userService.register(any(UserRequest.class))).thenReturn(mockResponse);

        // When
        MvcResult result = mockMvc.perform(post("/instructors/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.role").value("instructor"))
                .andReturn();

        // Then (JUnit Assertions)
        assertEquals(201, result.getResponse().getStatus());
        verify(userService).register(any(UserRequest.class));
    }

    @Test
    void register_InvalidRequestBody_ReturnsStatus400BadRequest() throws Exception {
        // Given: Invalid request payload failing @Valid constraints
        UserRequest invalidRequest = new UserRequest();

        // When & Then
        mockMvc.perform(post("/instructors/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        // Verify service was never reached due to validation interception
        verifyNoInteractions(userService);
    }

    // ==========================================
    // 2. Tests for /instructors/login
    // ==========================================

    @Test
    void login_Success_ReturnsStatus200Ok() throws Exception {
        // Given
        LoginRequest request = new LoginRequest();
        request.setEmail("john.doe@example.com");
        request.setPassword("Password123!");

        LoginResponse mockResponse = new LoginResponse();
        mockResponse.setToken("mocked-jwt-token");
        mockResponse.setUserId(1);
        mockResponse.setUserRole("instructor");

        when(authService.login(request.getEmail(), request.getPassword())).thenReturn(mockResponse);

        // When
        MvcResult result = mockMvc.perform(post("/instructors/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("mocked-jwt-token"))
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.userRole").value("instructor"))
                .andReturn();

        // Then (JUnit Assertions)
        assertEquals(200, result.getResponse().getStatus());
        assertNotNull(result.getResponse().getContentAsString());
        verify(authService).login(request.getEmail(), request.getPassword());
    }

    @Test
    void login_InvalidRequestBody_ReturnsStatus400BadRequest() throws Exception {
        // Given: Empty/Invalid request payload
        LoginRequest invalidRequest = new LoginRequest();

        // When & Then
        mockMvc.perform(post("/instructors/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(authService);
    }
}
