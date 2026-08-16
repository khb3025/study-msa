package com.example.userservice.domain;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;



public interface UserRepository extends JpaRepository<User, Long> {
    public Optional<User> findByEmail(String email);
}
