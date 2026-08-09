package com.example.boardservice.dto;

/**
 * UserDto
 */
public class UserDto {

    private Long userId;
    private String name;

    public UserDto() {
    }

    public UserDto(Long userId, String name) {
        this.userId = userId;
        this.name = name;
    }

    public Long getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

}
