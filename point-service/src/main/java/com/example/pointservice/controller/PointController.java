package com.example.pointservice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.pointservice.dto.AddPointRequestDto;
import com.example.pointservice.dto.DeductPointRequestDto;
import com.example.pointservice.service.PointService;

@RestController
@RequestMapping("/points")
public class PointController {
    private final PointService pointService;
    public PointController(PointService pointService) {
        this.pointService = pointService;
    }

    @PostMapping("/add")
    public ResponseEntity<Void> addPoints(
        @RequestBody AddPointRequestDto addPointRequestDto
    ) {
        pointService.addPoints(addPointRequestDto);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/deduct")
    public ResponseEntity<Void> deductPoints(
        @RequestBody DeductPointRequestDto deductPointRequestDto
    ) {
        pointService.deductPoints(deductPointRequestDto);
        return ResponseEntity.noContent().build();
    }
}
