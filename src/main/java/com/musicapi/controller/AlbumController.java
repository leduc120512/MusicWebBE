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
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

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

    @GetMapping({"", "/"})
    public ResponseEntity<?> getAlbums(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            Page<AlbumResponse> albums = albumService.getLatestAlbums(page, size);
            return ResponseEntity.ok(ApiResponse.success("Albums retrieved successfully", albums));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Error retrieving albums: " + e.getMessage()));
        }
    }

    @PostMapping(value = "", consumes = "multipart/form-data")
    public ResponseEntity<?> createAlbum(
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam(value = "coverImage", required = false) MultipartFile coverImage,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        try {
            String uploadDir = "D:/web nhac/duan1/upload/uploadalbums";
            Path dir = Paths.get(uploadDir);
            Files.createDirectories(dir);

            Album album = new Album();
            album.setTitle(title);
            album.setDescription(description);

            // 📷 Lưu ảnh nếu có
            if (coverImage != null && !coverImage.isEmpty()) {
                String sanitized = sanitizeFileName(coverImage.getOriginalFilename());
                String finalName = resolveUniqueFileName(dir, sanitized);

                Path filePath = dir.resolve(finalName);
                Files.copy(coverImage.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

                album.setCoverImage("/upload/uploadalbums/" + finalName); // đường dẫn frontend dùng
            }

            Album savedAlbum = albumService.createAlbum(album, currentUser);
            return ResponseEntity.ok(ApiResponse.success("Album created", savedAlbum));

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Error creating album: " + e.getMessage()));
        }
    }

    @PutMapping(value = "/{id}", consumes = "multipart/form-data")
    public ResponseEntity<?> updateAlbum(
            @PathVariable Long id,
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam(value = "coverImage", required = false) MultipartFile coverImage,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        try {
            String uploadDir = "D:/web nhac/duan1/upload/uploadalbums";
            Path dir = Paths.get(uploadDir);
            Files.createDirectories(dir);

            Album album = albumService.getByIdOrThrow(id);

            // 🎨 Nếu có ảnh mới
            if (coverImage != null && !coverImage.isEmpty()) {
                String sanitized = sanitizeFileName(coverImage.getOriginalFilename());
                String finalName = resolveUniqueFileName(dir, sanitized);

                Path filePath = dir.resolve(finalName);
                Files.copy(coverImage.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

                album.setCoverImage("/upload/uploadalbums/" + finalName);
            }

            // 📝 Cập nhật thông tin khác
            album.setTitle(title);
            album.setDescription(description);

            Album updated = albumService.updateAlbum(id, album, currentUser);
            return ResponseEntity.ok(ApiResponse.success("Album updated", updated));

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Error updating album: " + e.getMessage()));
        }
    }

    private String sanitizeFileName(String original) {
        if (original == null || original.isBlank()) return "file";
        int dot = original.lastIndexOf('.');
        String name = (dot > 0) ? original.substring(0, dot) : original;
        String ext  = (dot > 0) ? original.substring(dot) : "";

        // bỏ ký tự lạ, thay khoảng trắng -> '-', gộp nhiều '-'
        name = name.replaceAll("[^a-zA-Z0-9-_\\.]", "-")
                .replaceAll("-{2,}", "-")
                .toLowerCase();
        ext  = ext.replaceAll("[^a-zA-Z0-9\\.]", "").toLowerCase();

        if (name.isBlank()) name = "file";
        return name + ext;
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
