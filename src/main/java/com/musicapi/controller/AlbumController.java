package com.musicapi.controller;

import com.musicapi.dto.AlbumResponse;
import com.musicapi.dto.ApiResponse;
import com.musicapi.model.Album;
import com.musicapi.security.UserPrincipal;
import com.musicapi.service.AlbumService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/albums")
@CrossOrigin(origins = "*")
public class AlbumController {

    @Autowired
    private AlbumService albumService;

    @GetMapping("/latest")
    public ResponseEntity<?> getLatestAlbums(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            Page<AlbumResponse> albums = albumService.getLatestAlbums(page, size);
            return ResponseEntity.ok(ApiResponse.success("Latest albums retrieved successfully", albums));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Error retrieving albums: " + e.getMessage()));
        }
    }
    @PostMapping
    public ResponseEntity<?> createAlbum(
            @RequestBody Album album,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        try {
            Album savedAlbum = albumService.createAlbum(album, currentUser);
            return ResponseEntity.ok(ApiResponse.success("Album created", savedAlbum));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Error creating album: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateAlbum(
            @PathVariable Long id,
            @RequestBody Album album,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        try {
            Album updatedAlbum = albumService.updateAlbum(id, album, currentUser);
            return ResponseEntity.ok(ApiResponse.success("Album updated", updatedAlbum));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Error updating album: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAlbum(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        try {
            albumService.deleteAlbum(id, currentUser);
            return ResponseEntity.ok(ApiResponse.success("Album deleted", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Error deleting album: " + e.getMessage()));
        }
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchAlbums(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            Page<AlbumResponse> albums = albumService.searchAlbums(keyword, page, size);
            return ResponseEntity.ok(ApiResponse.success("Albums found", albums));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Error searching albums: " + e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getAlbumById(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        try {
            AlbumResponse album = albumService.getAlbumById(id, currentUser);
            return ResponseEntity.ok(ApiResponse.success("Album retrieved successfully", album));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Album not found: " + e.getMessage()));
        }
    }
}
