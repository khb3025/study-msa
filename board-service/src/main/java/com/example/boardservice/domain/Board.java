package com.example.boardservice.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Board {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long boardId;

    private String title;

    private String content;
    
    private Long userId; // MSA 환경에서는 FK를 걸수가 없음.

    public Board() {
    }

    public Board(String title, String content, Long userId) {
        this.title = title;
        this.content = content;
        this.userId = userId;
    }
    public Long getBoardId() {
        return boardId;
    }
    
    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public Long getUserId() {
        return userId;
    }
    
}
