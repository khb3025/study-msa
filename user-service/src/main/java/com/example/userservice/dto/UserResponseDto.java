package com.example.userservice.dto;

public class UserResponseDto {

    private Long userId;
    private String email;
    private String name;
    // 패스워드는 노출되면 안되는 정보
    
    public UserResponseDto(){
    }

    public UserResponseDto(Long userId, String email, String name) {
        this.userId = userId;
        this.email = email;
        this.name = name;
    }

    public Long getUserId() {
        return userId;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }
}
