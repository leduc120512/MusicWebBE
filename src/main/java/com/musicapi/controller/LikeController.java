package com.musicapi.controller;

import com.musicapi.dto.ApiResponse;
import com.musicapi.dto.SongResponse;
import com.musicapi.security.UserPrincipal;
import com.musicapi.service.LikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/likes")
@CrossOrigin(origins = "*")
public class LikeController {

    @Autowired
    private LikeService likeService;

    @PostMapping("/{songId}")
    public ResponseEntity<?> likeSong(
            @PathVariable Long songId,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        try {
            boolean created = likeService.likeSong(songId, currentUser.getId());
            String message = created ? "Liked song successfully" : "Song already liked";
            return ResponseEntity.ok(ApiResponse.success(message, Map.of(
                    "songId", songId,
                    "liked", true,
                    "likeCount", likeService.countLikes(songId)
            )));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Like failed: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{songId}")
    public ResponseEntity<?> unlikeSong(
            @PathVariable Long songId,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        try {
            boolean removed = likeService.unlikeSong(songId, currentUser.getId());
            String message = removed ? "Unliked song successfully" : "Song is not in liked list";
            return ResponseEntity.ok(ApiResponse.success(message, Map.of(
                    "songId", songId,
                    "liked", false,
                    "likeCount", likeService.countLikes(songId)
            )));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Unlike failed: " + e.getMessage()));
        }
    }

    @GetMapping("/status/{songId}")
    public ResponseEntity<?> getLikeStatus(
            @PathVariable Long songId,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        try {
            boolean liked = likeService.isLiked(songId, currentUser.getId());
            return ResponseEntity.ok(ApiResponse.success("Like status retrieved", Map.of(
                    "songId", songId,
                    "liked", liked,
                    "likeCount", likeService.countLikes(songId)
            )));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to get like status: " + e.getMessage()));
        }
    }

    @GetMapping("/song/{songId}/count")
    public ResponseEntity<?> getSongLikeCount(@PathVariable Long songId) {
        try {
            Long count = likeService.countLikes(songId);
            return ResponseEntity.ok(ApiResponse.success("Like count retrieved", Map.of(
                    "songId", songId,
                    "likeCount", count
            )));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to get like count: " + e.getMessage()));
        }
    }

    @GetMapping("/my")
    public ResponseEntity<?> getMyLikedSongs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        try {
            Page<SongResponse> songs = likeService.getLikedSongs(currentUser, page, size);
            return ResponseEntity.ok(ApiResponse.success("Liked songs retrieved successfully", songs));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to load liked songs: " + e.getMessage()));
        }
    }
}

