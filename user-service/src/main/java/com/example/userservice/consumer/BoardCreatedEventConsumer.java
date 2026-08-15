package com.example.userservice.consumer;

import java.util.Optional;

import org.springframework.kafka.annotation.BackOff;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.example.userservice.domain.User;
import com.example.userservice.domain.UserRepository;
import com.example.userservice.dto.AddActivityScoreRequestDto;
import com.example.userservice.event.BoardCreatedEvent;
import com.example.userservice.service.UserService;

import jakarta.transaction.Transactional;

@Component
public class BoardCreatedEventConsumer {

    private final UserService userService;
    
    public BoardCreatedEventConsumer(UserService userService) {
        this.userService = userService;
    }

    @KafkaListener(
        topics = "board-created",
        groupId = "user-service",
        concurrency = "3"
    )
    // @RetryableTopic(
    //     attempts = "3",
    //     backOff = @BackOff(
    //         delay=1000,
    //         multiplier=2
    //     ),
    //     dltTopicSuffix = ".dlt"
    // )
    public void consume(String message) {
        BoardCreatedEvent boardCreatedEvent =
            BoardCreatedEvent.fromJson(message);
        
        // 게시글 작성 시 활동 점수 10점 추가
        AddActivityScoreRequestDto addActivityScoreRequestDto =
            new AddActivityScoreRequestDto(
                boardCreatedEvent.getUserId(),
                10
        );

        userService.addActivityScore(addActivityScoreRequestDto);
        System.out.println("사용자 점수 적립 완료");
    }
}
