package com.example.boardservice.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.client.RestClient;

import com.example.boardservice.dto.UserResponseDto;

@Component
public class UserClient {
    private final RestClient restClient;
    
    public UserClient(
        @Value("${client.user-service.url}") String userServiceUrl
    ){
        this.restClient = RestClient.builder()
            .baseUrl(userServiceUrl)
            .build();
    }

    public UserResponseDto findById(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("userId must not be null");
        }

        return this.restClient.get()
            .uri("/users/{userId}", userId)
            .retrieve()
            .body(UserResponseDto.class);
    }
}
