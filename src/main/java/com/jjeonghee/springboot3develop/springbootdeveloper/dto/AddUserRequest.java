package com.jjeonghee.springboot3develop.springbootdeveloper.dto;

import lombok.Getter;

@Getter
public class AddUserRequest {
    public AddUserRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }

    private String email;
    private String password;
}
