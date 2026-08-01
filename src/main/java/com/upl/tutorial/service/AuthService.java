package com.upl.tutorial.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.upl.tutorial.security.JwtService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;

    private final JwtService jwtService;

    public String login(String username, String password) {
        try {
        
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password));
        

        if (authentication.isAuthenticated()) {
            log.info("**************Authenticated***********");
            String token = jwtService.generateToken(username);
            return token;
        } else {
            return "Authentication failed";
        }
            
        } catch (Exception e) {
            log.info("**************EXCEPTION***********");
        }
        return"failed";
    }

}
