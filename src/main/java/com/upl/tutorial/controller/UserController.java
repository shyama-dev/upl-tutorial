package com.upl.tutorial.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.upl.tutorial.dto.LoginRequest;
import com.upl.tutorial.dto.LoginResponse;
import com.upl.tutorial.dto.UserRequest;
import com.upl.tutorial.dto.UserResponse;
import com.upl.tutorial.service.AuthService;
import com.upl.tutorial.service.UserService;

import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("instructors")
@RequiredArgsConstructor
public class UserController {

    
    private final UserService service;
    private final AuthService authService;

    @SecurityRequirements(value = {})
    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody UserRequest request){
        UserResponse userResponse = service.register(request);

        return new ResponseEntity<>(userResponse, HttpStatus.CREATED); 

    }
    
    @SecurityRequirements(value = {})
    @PostMapping("/login")
     public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request){
        LoginResponse response=authService.login(request.getEmail(), request.getPassword());
        return new ResponseEntity<>(response,HttpStatus.OK);
     }
    
}
