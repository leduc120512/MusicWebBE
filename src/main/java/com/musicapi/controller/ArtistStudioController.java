package com.musicapi.controller;

import com.musicapi.dto.ApiResponse;
import com.musicapi.dto.ArtistNewsRequest;
import com.musicapi.dto.ArtistProfileUpdateRequest;
import com.musicapi.security.UserPrincipal;
import com.musicapi.service.ArtistNewsService;
import com.musicapi.service.ArtistProfileService;
import com.musicapi.service.DashboardService;
import com.musicapi.service.SongService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/artist-studio")
@CrossOrigin(origins = "*")
public class ArtistStudioController {

    @Autowired
    private ArtistProfileService artistProfileService;

    @Autowired
    private ArtistNewsService artistNewsService;

    @Autowired
    private DashboardService dashboardService;

    @Autowired
    private SongService songService;

    @GetMapping("/me/dashboard")
    public ResponseEntity<?> getMyDashboard(@AuthenticationPrincipal UserPrincipal currentUser) {
        try {
            return ResponseEntity.ok(ApiResponse.success("My dashboard stats retrieved successfully",
                    dashboardService.getMyArtistStats(currentUser.getId())));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Error retrieving dashboard stats: " + e.getMessage()));
        }
    }

    @GetMapping("/me/songs")
    public ResponseEntity<?> getMySongs(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        try {
            return ResponseEntity.ok(ApiResponse.success("My songs retrieved successfully",
                    songService.getMySongs(currentUser, page, size)));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Error retrieving my songs: " + e.getMessage()));
        }
    }

    @GetMapping("/me/profile")
    public ResponseEntity<?> getMyProfile(@AuthenticationPrincipal UserPrincipal currentUser) {
        try {
            return ResponseEntity.ok(ApiResponse.success("My artist profile retrieved successfully",
                    artistProfileService.getMyArtistProfile(currentUser.getId())));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Error retrieving my artist profile: " + e.getMessage()));
        }
    }

    @PutMapping("/me/profile")
    public ResponseEntity<?> updateMyProfile(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @Valid @RequestBody ArtistProfileUpdateRequest request) {
        try {
            return ResponseEntity.ok(ApiResponse.success("Artist profile updated successfully",
                    artistProfileService.upsertMyArtistProfile(currentUser.getId(), request)));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Error updating artist profile: " + e.getMessage()));
        }
    }

    @GetMapping("/me/news")
    public ResponseEntity<?> getMyNews(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Page<?> newsPage = artistNewsService.getMyNews(currentUser.getId(), PageRequest.of(page, size));
            return ResponseEntity.ok(ApiResponse.success("My artist news retrieved successfully", newsPage));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Error retrieving my artist news: " + e.getMessage()));
        }
    }

    @PostMapping("/me/news")
    public ResponseEntity<?> createMyNews(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @Valid @RequestBody ArtistNewsRequest request) {
        try {
            return ResponseEntity.ok(ApiResponse.success("Artist news created successfully",
                    artistNewsService.createMyNews(currentUser.getId(), request)));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Error creating artist news: " + e.getMessage()));
        }
    }

    @PutMapping("/me/news/{newsId}")
    public ResponseEntity<?> updateMyNews(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @PathVariable Long newsId,
            @Valid @RequestBody ArtistNewsRequest request) {
        try {
            return ResponseEntity.ok(ApiResponse.success("Artist news updated successfully",
                    artistNewsService.updateMyNews(currentUser.getId(), newsId, request)));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Error updating artist news: " + e.getMessage()));
        }
    }

    @DeleteMapping("/me/news/{newsId}")
    public ResponseEntity<?> deleteMyNews(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @PathVariable Long newsId) {
        try {
            artistNewsService.deleteMyNews(currentUser.getId(), newsId);
            return ResponseEntity.ok(ApiResponse.success("Artist news deleted successfully", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Error deleting artist news: " + e.getMessage()));
        }
    }
}

