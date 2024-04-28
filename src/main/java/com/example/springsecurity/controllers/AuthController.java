package com.example.springsecurity.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid; 

import com.example.springsecurity.controllers.dto.AuthLoginRequest;
import com.example.springsecurity.controllers.dto.AuthRegisterRequest;
import com.example.springsecurity.controllers.dto.AuthResponse;
import com.example.springsecurity.services.UserDetailServiceImpl;


@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserDetailServiceImpl userService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody @Valid AuthRegisterRequest authRegisterRequest) {
        return new ResponseEntity<>(userService.register(authRegisterRequest), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody @Valid AuthLoginRequest authLoginRequest) {
        return new ResponseEntity<>(userService.login(authLoginRequest), HttpStatus.OK);
    }

}
