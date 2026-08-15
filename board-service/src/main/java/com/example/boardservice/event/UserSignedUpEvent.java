package com.example.boardservice.event;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

public class UserSignedUpEvent {
    private Long userId;
    private String name;
    public UserSignedUpEvent(){

    }
    public UserSignedUpEvent(Long userId, String name) {
        this.userId = userId;
        this.name = name;
    }

    public Long getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }
    
    public static UserSignedUpEvent fromJson(String json) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(json, UserSignedUpEvent.class);
        }catch (JacksonException e) {
            e.printStackTrace();
            throw new RuntimeException("JSON 파싱 실패");
        }
    }
}
