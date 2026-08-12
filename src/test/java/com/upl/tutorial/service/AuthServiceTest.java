package com.upl.tutorial.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import com.upl.tutorial.dto.LoginResponse;
import com.upl.tutorial.model.UserRole;
import com.upl.tutorial.model.Users;
import com.upl.tutorial.repository.UserRepository;
import com.upl.tutorial.security.JwtService;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AuthService authService;

    // ==========================================
    // 1. Successful Login Scenario
    // ==========================================

    @Test
    void login_Success() {
        // Given
        String username = "john.doe@example.com";
        String password = "password123";
        String expectedToken = "mocked-jwt-token";

        Authentication authentication = mock(Authentication.class);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);

        when(jwtService.generateToken(username)).thenReturn(expectedToken);

        Users user = new Users();
        user.setuserId(10);
        user.setRole(UserRole.ADMIN); // Role enum will be converted to lower case "admin"

        when(userRepository.findByEmail(username)).thenReturn(Optional.of(user));

        // When
        LoginResponse response = authService.login(username, password);

        // Then
        assertNotNull(response);
        assertEquals(expectedToken, response.getToken());
        assertEquals(10, response.getUserId());
        assertEquals("admin", response.getUserRole());

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtService).generateToken(username);
        verify(userRepository).findByEmail(username);
    }

    // ==========================================
    // 2. Authentication Fails (Not Authenticated)
    // ==========================================

    @Test
    void login_NotAuthenticated_ThrowsBadCredentialsException() {
        // Given
        String username = "john.doe@example.com";
        String password = "wrongpassword";

        Authentication authentication = mock(Authentication.class);
        when(authentication.isAuthenticated()).thenReturn(false);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);

        // When & Then
        BadCredentialsException exception = assertThrows(
            BadCredentialsException.class,
            () -> authService.login(username, password)
        );

        assertEquals("Invalid username or password", exception.getMessage());
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

}
