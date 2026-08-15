package com.example.userservice.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.example.userservice.client.PointClient;
import com.example.userservice.domain.User;
import com.example.userservice.domain.UserRepository;
import com.example.userservice.dto.AddActivityScoreRequestDto;
import com.example.userservice.dto.SignUpRequestDto;
import com.example.userservice.dto.UserResponseDto;
import com.example.userservice.event.UserSignedUpEvent;

import jakarta.transaction.Transactional;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PointClient pointClient;
    private final KafkaTemplate<String, String> kafkaTemplate;
    public UserService(
        UserRepository userRepository,
        PointClient pointClient,
        KafkaTemplate<String, String> kafkaTemplate
    ) {
        this.userRepository = userRepository;
        this.pointClient = pointClient;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Transactional
    public void signUp(SignUpRequestDto signUpRequestDto) {
        User user = new User(
            signUpRequestDto.getEmail(),
            signUpRequestDto.getName(),
            signUpRequestDto.getPassword()
        );
        User savedUser = this.userRepository.save(user);
        
        // 회원가입 포인트 1000점 적립
        pointClient.addPoints(savedUser.getUserId(), 1000);

        // 회원가입 완료 이벤트 발행
        UserSignedUpEvent userSignedUpEvent = new UserSignedUpEvent(
            savedUser.getUserId(),
            savedUser.getName()
        );
        this.kafkaTemplate.send(
            "user-signed-up",
            this.toJsonString(userSignedUpEvent)
        );
    }

    public UserResponseDto getUser(Long id) {
        User user = this.userRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        
        return new UserResponseDto(
            user.getUserId(), 
            user.getEmail(),
            user.getName()
        );
    }

    public List<UserResponseDto> getUsersByIds(List<Long> ids) {
        List<User> users = this.userRepository.findAllById(ids);
        return users.stream()
            .map(user -> new UserResponseDto(
                user.getUserId(),
                user.getEmail(),
                user.getName()
            )).toList();
    }
    @Transactional
    public void addActivityScore(
        AddActivityScoreRequestDto addActivityScoreRequestDto
    ){
        User user = userRepository.findById(
            addActivityScoreRequestDto.getUserId()
        ).orElseThrow(() -> new IllegalArgumentException("사용자를 찾을수 없습니다."));

        user.addActivityScore(addActivityScoreRequestDto.getScore());
        userRepository.save(user);
        /* Eventual Consistency (최종적 일관성 확인 코드)
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        */
    }

    private String toJsonString(Object object){
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JacksonException e) {
            e.printStackTrace();
            throw new RuntimeException("JSON 직렬화 살패 ");
        }
    }

}
