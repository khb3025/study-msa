package com.example.boardservice.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import com.example.boardservice.dto.AddPointsRequestDto;
import com.example.boardservice.dto.DeductPointRequestDto;

@Component
public class PointClient {
    private RestClient restClient;
    public PointClient(@Value("${client.point-service.url}") String pointClientUrl){
        this.restClient = RestClient.builder()
            .baseUrl(pointClientUrl)
            .build();
    }

    public void deductPoints(Long userId, int amount){
        DeductPointRequestDto deductPointRequestDto =
            new DeductPointRequestDto(userId, amount);
        
        restClient.post()
            .uri("/points/deduct")
            .contentType(MediaType.APPLICATION_JSON)
            .body(deductPointRequestDto)
            .retrieve()
            .toBodilessEntity();
    }

    public void addPoints(Long userId, int amount){
        AddPointsRequestDto addPointsRequestDto =
            new AddPointsRequestDto(userId, amount);
        
        restClient.post()
            .uri("/points/add")
            .contentType(MediaType.APPLICATION_JSON)
            .body(addPointsRequestDto)
            .retrieve()
            .toBodilessEntity();
    }
}
