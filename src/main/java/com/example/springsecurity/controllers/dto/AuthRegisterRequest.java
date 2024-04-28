package com.example.springsecurity.controllers.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

public class AuthRegisterRequest {
    @NotBlank
    private String username;
    @NotBlank
    private String password;
    @Valid
    private AuthCreateRoleRequest roles;

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
    public AuthCreateRoleRequest getRoles() {
        return roles;
    }
    public void setRoles(AuthCreateRoleRequest roles) {
        this.roles = roles;
    }

}
