package com.musicapi.controller;

import com.musicapi.dto.ApiResponse;
import com.musicapi.dto.FavoriteAlbumRequest;
import com.musicapi.security.UserPrincipal;
import com.musicapi.service.FavoriteAlbumService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/favorite-albums")
@CrossOrigin(origins = "*")@Tag(name = "Favourite albums", description = "Personal collections (stored in the playlists table)")
public class FavoriteAlbumController {

    private final FavoriteAlbumService favoriteAlbumService;

    public FavoriteAlbumController(FavoriteAlbumService favoriteAlbumService) {
        this.favoriteAlbumService = favoriteAlbumService;
    }

    @GetMapping("/my")
    public ResponseEntity<?> getMyFavoriteAlbums(@AuthenticationPrincipal UserPrincipal currentUser) {
        return ResponseEntity.ok(ApiResponse.success(
                "Favorite albums retrieved successfully",
                favoriteAlbumService.getMyFavoriteAlbums(currentUser)
        ));
    }

    @GetMapping("/{albumId}")
    public ResponseEntity<?> getMyFavoriteAlbum(
            @PathVariable Long albumId,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        return ResponseEntity.ok(ApiResponse.success(
                "Favorite album retrieved successfully",
                favoriteAlbumService.getMyFavoriteAlbum(albumId, currentUser)
        ));
    }

    @PostMapping
    public ResponseEntity<?> create(
            @RequestBody FavoriteAlbumRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        return ResponseEntity.ok(ApiResponse.success(
                "Favorite album created successfully",
                favoriteAlbumService.create(request, currentUser)
        ));
    }

    @PutMapping("/{albumId}")
    public ResponseEntity<?> update(
            @PathVariable Long albumId,
            @RequestBody FavoriteAlbumRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        return ResponseEntity.ok(ApiResponse.success(
                "Favorite album updated successfully",
                favoriteAlbumService.update(albumId, request, currentUser)
        ));
    }

    @PostMapping("/{albumId}/songs/{songId}")
    public ResponseEntity<?> addSong(
            @PathVariable Long albumId,
            @PathVariable Long songId,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        return ResponseEntity.ok(ApiResponse.success(
                "Song added to favorite album successfully",
                favoriteAlbumService.addSong(albumId, songId, currentUser)
        ));
    }

    @DeleteMapping("/{albumId}/songs/{songId}")
    public ResponseEntity<?> removeSong(
            @PathVariable Long albumId,
            @PathVariable Long songId,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        return ResponseEntity.ok(ApiResponse.success(
                "Song removed from favorite album successfully",
                favoriteAlbumService.removeSong(albumId, songId, currentUser)
        ));
    }

    @DeleteMapping("/{albumId}")
    public ResponseEntity<?> delete(
            @PathVariable Long albumId,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        favoriteAlbumService.delete(albumId, currentUser);
        return ResponseEntity.ok(ApiResponse.success("Favorite album deleted successfully", null));
    }
}
