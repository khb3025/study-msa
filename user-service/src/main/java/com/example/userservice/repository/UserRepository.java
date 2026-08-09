package com.example.userservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.userservice.domain.User;


public interface UserRepository extends JpaRepository<User, Long> {
    
    
}
