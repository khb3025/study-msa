package com.example.pointservice.dto;

public class AddPointRequestDto {
    private Long userId;
    private int amount;
    
    public AddPointRequestDto() {
    }

    public AddPointRequestDto(Long userId, int amount) {
        this.userId = userId;
        this.amount = amount;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}
