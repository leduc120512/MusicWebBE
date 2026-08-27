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
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/artist-studio")
@CrossOrigin(origins = "*")@Tag(name = "Artist studio", description = "Self-service area for ROLE_AUTHOR")
public class ArtistStudioController {

    private final ArtistProfileService artistProfileService;
    private final ArtistNewsService artistNewsService;
    private final DashboardService dashboardService;
    private final SongService songService;

    public ArtistStudioController(
            ArtistProfileService artistProfileService,
            ArtistNewsService artistNewsService,
            DashboardService dashboardService,
            SongService songService
    ) {
        this.artistProfileService = artistProfileService;
        this.artistNewsService = artistNewsService;
        this.dashboardService = dashboardService;
        this.songService = songService;
    }

    @GetMapping("/me/dashboard")
    public ResponseEntity<?> getMyDashboard(@AuthenticationPrincipal UserPrincipal currentUser) {
        return ResponseEntity.ok(ApiResponse.success("My dashboard stats retrieved successfully",
                dashboardService.getMyArtistStats(currentUser.getId())));
    }

    @GetMapping("/me/songs")
    public ResponseEntity<?> getMySongs(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return ResponseEntity.ok(ApiResponse.success("My songs retrieved successfully",
                songService.getMySongs(currentUser, page, size)));
    }

    @GetMapping("/me/profile")
    public ResponseEntity<?> getMyProfile(@AuthenticationPrincipal UserPrincipal currentUser) {
        return ResponseEntity.ok(ApiResponse.success("My artist profile retrieved successfully",
                artistProfileService.getMyArtistProfile(currentUser.getId())));
    }

    @PutMapping("/me/profile")
    public ResponseEntity<?> updateMyProfile(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @Valid @RequestBody ArtistProfileUpdateRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Artist profile updated successfully",
                artistProfileService.upsertMyArtistProfile(currentUser.getId(), request)));
    }

    @GetMapping("/me/news")
    public ResponseEntity<?> getMyNews(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<?> newsPage = artistNewsService.getMyNews(currentUser.getId(), PageRequest.of(page, size));
        return ResponseEntity.ok(ApiResponse.success("My artist news retrieved successfully", newsPage));
    }

    @PostMapping("/me/news")
    public ResponseEntity<?> createMyNews(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @Valid @RequestBody ArtistNewsRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Artist news created successfully",
                artistNewsService.createMyNews(currentUser.getId(), request)));
    }

    @PutMapping("/me/news/{newsId}")
    public ResponseEntity<?> updateMyNews(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @PathVariable Long newsId,
            @Valid @RequestBody ArtistNewsRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Artist news updated successfully",
                artistNewsService.updateMyNews(currentUser.getId(), newsId, request)));
    }

    @DeleteMapping("/me/news/{newsId}")
    public ResponseEntity<?> deleteMyNews(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @PathVariable Long newsId) {
        artistNewsService.deleteMyNews(currentUser.getId(), newsId);
        return ResponseEntity.ok(ApiResponse.success("Artist news deleted successfully", null));
    }
}

