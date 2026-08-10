package com.example.pointservice.domain;

import jakarta.annotation.Generated;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Point {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pointId;
    private Long userId;
    private int amount;
    
    public Point() {
    }

    public Point(Long userId, int getAmount) {
        this.userId = userId;
        this.amount = getAmount;
    }

    public Long getPointId() {
        return pointId;
    }

    public Long getUserId() {
        return userId;
    }

    public int getAmount() {
        return amount;
    }

    public void addAmount(int amount) {
        this.amount += amount;
    }

    public void deductAmount(int amount) {
        this.amount -= amount;
    }
}
