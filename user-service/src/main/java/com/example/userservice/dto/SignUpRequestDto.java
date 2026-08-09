package com.example.userservice.dto;

public class SignUpRequestDto {
    private String email;
    private String name;
    private String password;

    public SignUpRequestDto() {
    }

    public SignUpRequestDto(String email, String name, String password) {
        this.email = email;
        this.name = name;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }
}
