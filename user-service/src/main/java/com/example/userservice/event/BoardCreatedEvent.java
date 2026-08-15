package com.example.userservice.event;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

public class BoardCreatedEvent {
    
    private Long userId;

    public BoardCreatedEvent() {}
    public BoardCreatedEvent(Long userId) {
        this.userId = userId;
    }

    public Long getUserId() {
        return userId;
    }

    public static BoardCreatedEvent fromJson(String message) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(
                message, 
                BoardCreatedEvent.class
            );
        } catch (JacksonException e) {
            e.printStackTrace();
            throw new RuntimeException("Json 파싱 실패");
        }
    }
}
