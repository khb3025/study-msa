package com.example.userservice.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriBuilder;

import com.example.userservice.dto.AddPointRequestwDto;

import ch.qos.logback.core.util.ContentTypeUtil;

@Component
public class PointClient {
    private final RestClient restClient;
    
    public PointClient(@Value("${client.point-service.url}") String pointServiceUrl) {
        this.restClient = RestClient.builder()
            .baseUrl(pointServiceUrl)
            .build();
    }

    public void addPoints(Long userId, int point){
        AddPointRequestwDto addPointRequestwDto =
            new AddPointRequestwDto(
                userId,
                point
            );

        restClient.post()
            .uri("/points/add")
            .contentType(MediaType.APPLICATION_JSON)
            .body(addPointRequestwDto)
            .retrieve()
            .toBodilessEntity();
    }

}
