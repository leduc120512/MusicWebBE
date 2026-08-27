package com.musicapi.controller;

import com.musicapi.dto.ApiResponse;
import com.musicapi.security.UserPrincipal;
import com.musicapi.service.UserRecommendationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/recommendations")
@CrossOrigin(origins = "*")@Tag(name = "Recommendations", description = "AI-assisted user recommendations")
public class RecommendationController {

    private final UserRecommendationService userRecommendationService;

    public RecommendationController(UserRecommendationService userRecommendationService) {
        this.userRecommendationService = userRecommendationService;
    }

    @GetMapping("/users")
    public ResponseEntity<?> recommendUsers(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(required = false) String model
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                "Recommended users retrieved successfully",
                userRecommendationService.recommendUsers(currentUser.getId(), limit, model)
        ));
    }
}
