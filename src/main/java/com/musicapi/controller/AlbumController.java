package com.musicapi.controller;

import com.musicapi.dto.AlbumResponse;
import com.musicapi.service.FileStorageService;
import com.musicapi.dto.ApiResponse;
import com.musicapi.model.Album;
import com.musicapi.security.UserPrincipal;
import com.musicapi.service.AlbumService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;

@RestController
@RequestMapping("/api/albums")
@CrossOrigin(origins = "*")@Tag(name = "Albums", description = "Public album catalogue and artist album management")
public class AlbumController {

    private final AlbumService albumService;

    private final FileStorageService fileStorageService;

    public AlbumController(AlbumService albumService, FileStorageService fileStorageService) {
        this.albumService = albumService;
        this.fileStorageService = fileStorageService;
    }


    @GetMapping("/latest")
    public ResponseEntity<?> getLatestAlbums(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<AlbumResponse> albums = albumService.getLatestAlbums(page, size);
        return ResponseEntity.ok(ApiResponse.success("Latest albums retrieved successfully", albums));
    }

    @GetMapping({"", "/"})
    public ResponseEntity<?> getAlbums(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<AlbumResponse> albums = albumService.getLatestAlbums(page, size);
        return ResponseEntity.ok(ApiResponse.success("Albums retrieved successfully", albums));
    }

    @PostMapping(value = "", consumes = "multipart/form-data")
    public ResponseEntity<?> createAlbum(
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam(value = "coverImage", required = false) MultipartFile coverImage,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        Album album = new Album();
        album.setTitle(title);
        album.setDescription(description);

        String coverPath = fileStorageService.store(coverImage, "uploadalbums");
        if (coverPath != null) {
            album.setCoverImage(coverPath);
        }

        Album savedAlbum = albumService.createAlbum(album, currentUser);
        return ResponseEntity.ok(ApiResponse.success("Album created", savedAlbum));
    }

    @PutMapping(value = "/{id}", consumes = "multipart/form-data")
    public ResponseEntity<?> updateAlbum(
            @PathVariable Long id,
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam(value = "coverImage", required = false) MultipartFile coverImage,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        Album album = albumService.getByIdOrThrow(id);

        String coverPath = fileStorageService.store(coverImage, "uploadalbums");
        if (coverPath != null) {
            album.setCoverImage(coverPath);
        }

        // copy the remaining fields
        album.setTitle(title);
        album.setDescription(description);

        Album updated = albumService.updateAlbum(id, album, currentUser);
        return ResponseEntity.ok(ApiResponse.success("Album updated", updated));
    }

private String resolveUniqueFileName(Path dir, String sanitized) {
        int dot = sanitized.lastIndexOf('.');
        String base = (dot > 0) ? sanitized.substring(0, dot) : sanitized;
        String ext  = (dot > 0) ? sanitized.substring(dot) : "";

        Path p = dir.resolve(sanitized);
        int i = 1;
        while (Files.exists(p)) {
            p = dir.resolve(base + "-" + i + ext);
            i++;
        }
        return p.getFileName().toString();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAlbum(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        albumService.deleteAlbum(id, currentUser);
        return ResponseEntity.ok(ApiResponse.success("Album deleted", null));
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchAlbums(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<AlbumResponse> albums = albumService.searchAlbums(keyword, page, size);
        return ResponseEntity.ok(ApiResponse.success("Albums found", albums));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getAlbumById(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        AlbumResponse album = albumService.getAlbumById(id, currentUser);
        return ResponseEntity.ok(ApiResponse.success("Album retrieved successfully", album));
    }
}
