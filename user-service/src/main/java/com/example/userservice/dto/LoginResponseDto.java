package com.example.userservice.dto;

public class LoginResponseDto {
    
    private String token;
    
    protected LoginResponseDto() {}

    public LoginResponseDto(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }
    
}
