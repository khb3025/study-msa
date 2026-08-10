package com.example.userservice.dto;

public class AddPointRequestwDto {
    private Long userId;
    private int amount;

    public AddPointRequestwDto() {
    }
    
    public AddPointRequestwDto(Long userId, int amount) {
        this.userId = userId;
        this.amount = amount;
    }

    public Long getUserId() {
        return userId;
    }
    public int getAmount() {
        return amount;
    }

    
}
