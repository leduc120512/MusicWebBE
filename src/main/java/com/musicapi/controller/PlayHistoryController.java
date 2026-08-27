package com.musicapi.controller;

import com.musicapi.dto.ApiResponse;
import com.musicapi.security.UserPrincipal;
import com.musicapi.service.PlayHistoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * HTTP layer for listening history. All rules live in {@link PlayHistoryService}.
 */
@RestController
@RequestMapping("/api/history")
@CrossOrigin(origins = "*")
@Tag(name = "Play history", description = "What the signed-in user has listened to")
public class PlayHistoryController {

    private final PlayHistoryService playHistoryService;

    public PlayHistoryController(PlayHistoryService playHistoryService) {
        this.playHistoryService = playHistoryService;
    }

    @GetMapping("/recent")
    @Operation(summary = "Recently played songs, newest first")
    public ResponseEntity<?> getRecentlyPlayed(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                "Recently played songs retrieved successfully",
                playHistoryService.getRecentlyPlayed(currentUser, page, size)));
    }

    @GetMapping("/count/song/{songId}")
    @Operation(summary = "How many times a song has been played, across all users")
    public ResponseEntity<?> countPlaysBySong(@PathVariable Long songId) {
        return ResponseEntity.ok(ApiResponse.success(
                "Play count retrieved successfully", playHistoryService.countPlaysBySong(songId)));
    }

    @GetMapping("/count/user")
    @Operation(summary = "How many plays the signed-in caller has recorded")
    public ResponseEntity<?> countPlaysByUser(@AuthenticationPrincipal UserPrincipal currentUser) {
        return ResponseEntity.ok(ApiResponse.success(
                "User play count retrieved successfully",
                playHistoryService.countPlaysByUser(currentUser.getId())));
    }

    @DeleteMapping("/clear")
    @Operation(summary = "Delete the caller's entire listening history")
    public ResponseEntity<?> clearHistory(@AuthenticationPrincipal UserPrincipal currentUser) {
        playHistoryService.clearHistory(currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success("Play history cleared successfully", null));
    }

    @DeleteMapping("/remove")
    @Operation(summary = "Remove one song from the caller's history")
    public ResponseEntity<?> removeSong(
            @RequestParam Long songId, @AuthenticationPrincipal UserPrincipal currentUser) {
        playHistoryService.removeSong(currentUser.getId(), songId);
        return ResponseEntity.ok(ApiResponse.success("Song removed from play history", null));
    }

    @PutMapping("/update/{historyId}")
    @Operation(summary = "Repoint or re-time one history entry (maintenance)")
    public ResponseEntity<?> updatePlayHistory(
            @PathVariable Long historyId,
            @RequestParam(required = false) Long newSongId,
            @RequestParam(required = false) String newPlayedAt
    ) {
        LocalDateTime playedAt = newPlayedAt == null
                ? null
                : LocalDateTime.parse(newPlayedAt, DateTimeFormatter.ISO_DATE_TIME);

        playHistoryService.updatePlayHistory(historyId, newSongId, playedAt);
        return ResponseEntity.ok(ApiResponse.success("Play history updated successfully", null));
    }
}
