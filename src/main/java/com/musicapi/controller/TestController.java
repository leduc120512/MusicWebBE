package com.musicapi.controller;

import com.musicapi.dto.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class TestController {

    @GetMapping("/test")
    public ResponseEntity<?> test() {
        Map<String, Object> data = new HashMap<>();
        data.put("message", "Music API is running!");
        data.put("timestamp", LocalDateTime.now());
        data.put("version", "1.0.0");
        
        return ResponseEntity.ok(ApiResponse.success("API is working", data));
    }

    @GetMapping("/health")
    public ResponseEntity<?> health() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("timestamp", LocalDateTime.now());
        
        return ResponseEntity.ok(ApiResponse.success("Service is healthy", health));
    }
}
