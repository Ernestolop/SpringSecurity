package com.example.springsecurity.controllers.dto;

import jakarta.validation.constraints.NotBlank;

public class AuthLoginRequest {

    @NotBlank
    private String username;
    @NotBlank
    private String password;

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

}
