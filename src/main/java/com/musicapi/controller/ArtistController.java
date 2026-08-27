package com.musicapi.controller;

import com.musicapi.dto.ApiResponse;
import com.musicapi.service.ArtistNewsService;
import com.musicapi.service.ArtistProfileService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/artists")
@CrossOrigin(origins = "*")@Tag(name = "Artists (public)", description = "Public artist profiles and news")
public class ArtistController {

    private final ArtistProfileService artistProfileService;
    private final ArtistNewsService artistNewsService;

    public ArtistController(ArtistProfileService artistProfileService, ArtistNewsService artistNewsService) {
        this.artistProfileService = artistProfileService;
        this.artistNewsService = artistNewsService;
    }

    @GetMapping("/{artistId}/profile")
    public ResponseEntity<?> getArtistProfile(@PathVariable Long artistId) {
        return ResponseEntity.ok(ApiResponse.success("Artist profile retrieved successfully",
                artistProfileService.getPublicArtistProfile(artistId)));
    }

    @GetMapping("/{artistId}/news")
    public ResponseEntity<?> getArtistNews(
            @PathVariable Long artistId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<?> newsPage = artistNewsService.getPublicNews(artistId, PageRequest.of(page, size));
        return ResponseEntity.ok(ApiResponse.success("Artist news retrieved successfully", newsPage));
    }

    @GetMapping("/{artistId}/news/{newsId}")
    public ResponseEntity<?> getArtistNewsDetail(
            @PathVariable Long artistId,
            @PathVariable Long newsId) {
        return ResponseEntity.ok(ApiResponse.success("Artist news detail retrieved successfully",
                artistNewsService.getPublicNewsDetail(artistId, newsId)));
    }
}

