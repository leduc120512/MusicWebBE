package com.musicapi.controller;

import com.musicapi.dto.ApiResponse;
import com.musicapi.security.UserPrincipal;
import com.musicapi.service.UserRecommendationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/recommendations")
@CrossOrigin(origins = "*")
public class RecommendationController {
    @Autowired
    private UserRecommendationService userRecommendationService;

    @GetMapping("/users")
    public ResponseEntity<?> recommendUsers(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(required = false) String model
    ) {
        try {
            return ResponseEntity.ok(ApiResponse.success(
                    "Recommended users retrieved successfully",
                    userRecommendationService.recommendUsers(currentUser.getId(), limit, model)
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to recommend users: " + e.getMessage()));
        }
    }
}
