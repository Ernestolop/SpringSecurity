package com.example.springsecurity.controllers.dto;

import java.util.List;

import jakarta.validation.constraints.Size;

public class AuthCreateRoleRequest {
    @Size(min = 1, max = 50)
    private List<String> roleList;

    public List<String> getRoleList() {
        return roleList;
    }

    public void setRoleList(List<String> roleList) {
        this.roleList = roleList;
    }
}
