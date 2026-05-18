package com.musicapi.controller;

import com.musicapi.dto.ApiResponse;
import com.musicapi.dto.FavoriteAlbumRequest;
import com.musicapi.security.UserPrincipal;
import com.musicapi.service.FavoriteAlbumService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/favorite-albums")
@CrossOrigin(origins = "*")
public class FavoriteAlbumController {
    @Autowired
    private FavoriteAlbumService favoriteAlbumService;

    @GetMapping("/my")
    public ResponseEntity<?> getMyFavoriteAlbums(@AuthenticationPrincipal UserPrincipal currentUser) {
        try {
            return ResponseEntity.ok(ApiResponse.success(
                    "Favorite albums retrieved successfully",
                    favoriteAlbumService.getMyFavoriteAlbums(currentUser)
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to load favorite albums: " + e.getMessage()));
        }
    }

    @GetMapping("/{albumId}")
    public ResponseEntity<?> getMyFavoriteAlbum(
            @PathVariable Long albumId,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        try {
            return ResponseEntity.ok(ApiResponse.success(
                    "Favorite album retrieved successfully",
                    favoriteAlbumService.getMyFavoriteAlbum(albumId, currentUser)
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to load favorite album: " + e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<?> create(
            @RequestBody FavoriteAlbumRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        try {
            return ResponseEntity.ok(ApiResponse.success(
                    "Favorite album created successfully",
                    favoriteAlbumService.create(request, currentUser)
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to create favorite album: " + e.getMessage()));
        }
    }

    @PutMapping("/{albumId}")
    public ResponseEntity<?> update(
            @PathVariable Long albumId,
            @RequestBody FavoriteAlbumRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        try {
            return ResponseEntity.ok(ApiResponse.success(
                    "Favorite album updated successfully",
                    favoriteAlbumService.update(albumId, request, currentUser)
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to update favorite album: " + e.getMessage()));
        }
    }

    @PostMapping("/{albumId}/songs/{songId}")
    public ResponseEntity<?> addSong(
            @PathVariable Long albumId,
            @PathVariable Long songId,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        try {
            return ResponseEntity.ok(ApiResponse.success(
                    "Song added to favorite album successfully",
                    favoriteAlbumService.addSong(albumId, songId, currentUser)
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to add song: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{albumId}/songs/{songId}")
    public ResponseEntity<?> removeSong(
            @PathVariable Long albumId,
            @PathVariable Long songId,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        try {
            return ResponseEntity.ok(ApiResponse.success(
                    "Song removed from favorite album successfully",
                    favoriteAlbumService.removeSong(albumId, songId, currentUser)
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to remove song: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{albumId}")
    public ResponseEntity<?> delete(
            @PathVariable Long albumId,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        try {
            favoriteAlbumService.delete(albumId, currentUser);
            return ResponseEntity.ok(ApiResponse.success("Favorite album deleted successfully", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to delete favorite album: " + e.getMessage()));
        }
    }
}
