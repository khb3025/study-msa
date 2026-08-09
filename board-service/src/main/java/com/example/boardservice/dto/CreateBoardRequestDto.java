package com.example.boardservice.dto;

public class CreateBoardRequestDto {
    private String title;
    private String content;
    private Long userId;

    public CreateBoardRequestDto() {
    }

    public CreateBoardRequestDto(String title, String content, Long userId) {
        this.title = title;
        this.content = content;
        this.userId = userId;
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
