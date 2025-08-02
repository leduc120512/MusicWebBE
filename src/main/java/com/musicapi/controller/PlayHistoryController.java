package com.musicapi.controller;

import com.musicapi.dto.ApiResponse;
import com.musicapi.dto.SongResponse;
import com.musicapi.model.Song;
import com.musicapi.model.User;
import com.musicapi.repository.PlayHistoryRepository;
import com.musicapi.repository.SongRepository;
import com.musicapi.repository.UserRepository;
import com.musicapi.security.UserPrincipal;
import com.musicapi.service.PlayHistoryService;
import com.musicapi.service.SongService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/api/history")
@CrossOrigin(origins = "*")
public class PlayHistoryController {

    @Autowired
    private PlayHistoryRepository playHistoryRepository;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PlayHistoryService playHistoryService;
    @Autowired
    private SongRepository songRepository;

    @Autowired
    private SongService songService;

    @GetMapping("/recent")
    public ResponseEntity<?> getRecentlyPlayed(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        try {
            User user = userRepository.findById(currentUser.getId())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            Pageable pageable = PageRequest.of(page, size);
            Page<Song> songs = playHistoryRepository.findRecentlyPlayedByUser(user, pageable);

            Page<SongResponse> response = songService.convertToSongResponsePage(songs, currentUser);
            return ResponseEntity.ok(ApiResponse.success("Recently played songs retrieved successfully", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Error retrieving play history: " + e.getMessage()));
        }
    }

    @GetMapping("/count/song/{songId}")
    public ResponseEntity<?> countPlaysBySong(@PathVariable Long songId) {
        try {
            Song song = songRepository.findById(songId)
                    .orElseThrow(() -> new RuntimeException("Song not found"));
            Long count = playHistoryRepository.countPlaysBySong(song);
            return ResponseEntity.ok(ApiResponse.success("Play count retrieved", count));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Error counting song plays: " + e.getMessage()));
        }
    }

    @GetMapping("/count/user")
    public ResponseEntity<?> countPlaysByUser(@AuthenticationPrincipal UserPrincipal currentUser) {
        try {
            User user = userRepository.findById(currentUser.getId())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            Long count = playHistoryRepository.countPlaysByUser(user);
            return ResponseEntity.ok(ApiResponse.success("User play count retrieved", count));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Error counting user plays: " + e.getMessage()));
        }
    }


    @DeleteMapping("/clear")
    public ResponseEntity<?> clearUserHistory(@AuthenticationPrincipal UserPrincipal currentUser) {
        try {
            User user = userRepository.findById(currentUser.getId())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            playHistoryService.deleteAllByUser(user);
            return ResponseEntity.ok(ApiResponse.success("Play history cleared", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Error clearing play history: " + e.getMessage()));
        }
    }

    @DeleteMapping("/remove")
    public ResponseEntity<?> removeSongFromHistory(
            @RequestParam Long songId,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        try {
            User user = userRepository.findById(currentUser.getId())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            Song song = songRepository.findById(songId)
                    .orElseThrow(() -> new RuntimeException("Song not found"));
            playHistoryService.deleteByUserAndSong(user, song);
            return ResponseEntity.ok(ApiResponse.success("Song removed from play history", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Error removing song: " + e.getMessage()));
        }
    }

    @PutMapping("/update/{historyId}")
    public ResponseEntity<?> updatePlayHistory(
            @PathVariable Long historyId,
            @RequestParam(required = false) Long newSongId,
            @RequestParam(required = false) String newPlayedAt // ISO-8601: 2025-07-29T10:00
    ) {
        try {
            Song newSong = null;
            if (newSongId != null) {
                newSong = songRepository.findById(newSongId)
                        .orElseThrow(() -> new RuntimeException("Song not found"));
            }

            LocalDateTime playedAt = null;
            if (newPlayedAt != null) {
                playedAt = LocalDateTime.parse(newPlayedAt, DateTimeFormatter.ISO_DATE_TIME);
            }

            playHistoryService.updatePlayHistory(historyId, newSong, playedAt);
            return ResponseEntity.ok(ApiResponse.success("Play history updated", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Error updating play history: " + e.getMessage()));
        }
    }
}
