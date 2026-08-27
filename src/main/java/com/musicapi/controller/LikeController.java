package com.musicapi.controller;

import com.musicapi.dto.ApiResponse;
import com.musicapi.dto.SongResponse;
import com.musicapi.security.UserPrincipal;
import com.musicapi.service.LikeService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/likes")
@CrossOrigin(origins = "*")@Tag(name = "Likes", description = "Liking songs")
public class LikeController {

    private final LikeService likeService;

    public LikeController(LikeService likeService) {
        this.likeService = likeService;
    }

    @PostMapping("/{songId}")
    public ResponseEntity<?> likeSong(
            @PathVariable Long songId,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        boolean created = likeService.likeSong(songId, currentUser.getId());
        String message = created ? "Liked song successfully" : "Song already liked";
        return ResponseEntity.ok(ApiResponse.success(message, Map.of(
                "songId", songId,
                "liked", true,
                "likeCount", likeService.countLikes(songId)
        )));
    }

    @DeleteMapping("/{songId}")
    public ResponseEntity<?> unlikeSong(
            @PathVariable Long songId,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        boolean removed = likeService.unlikeSong(songId, currentUser.getId());
        String message = removed ? "Unliked song successfully" : "Song is not in liked list";
        return ResponseEntity.ok(ApiResponse.success(message, Map.of(
                "songId", songId,
                "liked", false,
                "likeCount", likeService.countLikes(songId)
        )));
    }

    @GetMapping("/status/{songId}")
    public ResponseEntity<?> getLikeStatus(
            @PathVariable Long songId,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        boolean liked = likeService.isLiked(songId, currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success("Like status retrieved", Map.of(
                "songId", songId,
                "liked", liked,
                "likeCount", likeService.countLikes(songId)
        )));
    }

    @GetMapping("/song/{songId}/count")
    public ResponseEntity<?> getSongLikeCount(@PathVariable Long songId) {
        Long count = likeService.countLikes(songId);
        return ResponseEntity.ok(ApiResponse.success("Like count retrieved", Map.of(
                "songId", songId,
                "likeCount", count
        )));
    }

    @GetMapping("/my")
    public ResponseEntity<?> getMyLikedSongs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        Page<SongResponse> songs = likeService.getLikedSongs(currentUser, page, size);
        return ResponseEntity.ok(ApiResponse.success("Liked songs retrieved successfully", songs));
    }
}

