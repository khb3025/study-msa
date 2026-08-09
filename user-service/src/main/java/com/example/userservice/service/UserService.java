package com.example.userservice.service;

import org.springframework.stereotype.Service;

import com.example.userservice.domain.User;
import com.example.userservice.domain.UserRepository;
import com.example.userservice.dto.SignUpRequestDto;

import jakarta.transaction.Transactional;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    @Transactional
    public void signUp(SignUpRequestDto signUpRequestDto) {
        User user = new User(
            signUpRequestDto.getEmail(),
            signUpRequestDto.getName(),
            signUpRequestDto.getPassword()
        );
        this.userRepository.save(user);
        
    }
}
