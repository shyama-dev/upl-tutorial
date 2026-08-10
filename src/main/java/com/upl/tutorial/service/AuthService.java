package com.upl.tutorial.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import com.upl.tutorial.dto.LoginResponse;
import com.upl.tutorial.security.JwtService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;

    private final JwtService jwtService;

    public LoginResponse login(String username, String password) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password));
        LoginResponse loginResponse = new LoginResponse();

        if (!authentication.isAuthenticated()) {
            throw new BadCredentialsException("Invalid username or password");
        }
        log.info("**************Authenticated***********");
        String token = jwtService.generateToken(username);
        String role = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority)
                .findFirst().orElseThrow(() -> new RuntimeException("User role not found"));
        log.info("**************ROLE ***********"+role);

        loginResponse.setToken(token);
        loginResponse.setUserRole(role);
        return loginResponse;

    }

}
