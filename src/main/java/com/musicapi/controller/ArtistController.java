package com.musicapi.controller;

import com.musicapi.dto.ApiResponse;
import com.musicapi.service.ArtistNewsService;
import com.musicapi.service.ArtistProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/artists")
@CrossOrigin(origins = "*")
public class ArtistController {

    @Autowired
    private ArtistProfileService artistProfileService;

    @Autowired
    private ArtistNewsService artistNewsService;

    @GetMapping("/{artistId}/profile")
    public ResponseEntity<?> getArtistProfile(@PathVariable Long artistId) {
        try {
            return ResponseEntity.ok(ApiResponse.success("Artist profile retrieved successfully",
                    artistProfileService.getPublicArtistProfile(artistId)));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Error retrieving artist profile: " + e.getMessage()));
        }
    }

    @GetMapping("/{artistId}/news")
    public ResponseEntity<?> getArtistNews(
            @PathVariable Long artistId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Page<?> newsPage = artistNewsService.getPublicNews(artistId, PageRequest.of(page, size));
            return ResponseEntity.ok(ApiResponse.success("Artist news retrieved successfully", newsPage));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Error retrieving artist news: " + e.getMessage()));
        }
    }

    @GetMapping("/{artistId}/news/{newsId}")
    public ResponseEntity<?> getArtistNewsDetail(
            @PathVariable Long artistId,
            @PathVariable Long newsId) {
        try {
            return ResponseEntity.ok(ApiResponse.success("Artist news detail retrieved successfully",
                    artistNewsService.getPublicNewsDetail(artistId, newsId)));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Error retrieving artist news detail: " + e.getMessage()));
        }
    }
}

