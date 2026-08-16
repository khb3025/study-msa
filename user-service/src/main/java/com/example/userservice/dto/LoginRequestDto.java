package com.example.userservice.dto;

public class LoginRequestDto {
    private String email;
    private String password;

    protected LoginRequestDto() {}

    public LoginRequestDto(
        String email, 
        String password
    ) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

}
