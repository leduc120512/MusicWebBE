package com.musicapi.controller;

import com.musicapi.dto.ApiResponse;
import com.musicapi.dto.SongResponse;
import com.musicapi.model.Album;
import com.musicapi.model.Genre;
import com.musicapi.model.Song;
import com.musicapi.security.UserPrincipal;
import com.musicapi.service.SongService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.multipart.MultipartFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
import java.util.List;

@RestController
@RequestMapping("/api/songs")
@CrossOrigin(origins = "*")
public class SongController {

    @Autowired
    private SongService songService;

    @GetMapping("/latest")
    public ResponseEntity<?> getLatestSongs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        try {
            Page<SongResponse> songs = songService.getLatestSongs(page, size, currentUser);
            return ResponseEntity.ok(ApiResponse.success("Latest songs retrieved successfully", songs));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Error retrieving songs: " + e.getMessage()));
        }
    }

    @GetMapping("/popular")
    public ResponseEntity<?> getPopularSongs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        try {
            Page<SongResponse> songs = songService.getPopularSongs(page, size, currentUser);
            return ResponseEntity.ok(ApiResponse.success("Popular songs retrieved successfully", songs));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Error retrieving popular songs: " + e.getMessage()));
        }
    }
    @GetMapping("/public/search-suggestions")
    public ResponseEntity<?> getSearchSuggestions(@RequestParam(required = false) String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Missing keyword parameter"));
        }

        try {
            List<SongResponse> suggestions = songService.searchTopSongs(keyword);
            return ResponseEntity.ok(ApiResponse.success("Suggestions retrieved", suggestions));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Error retrieving suggestions: " + e.getMessage()));
        }
    }


    @GetMapping("/search")
    public ResponseEntity<?> searchSongs(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        try {
            Page<SongResponse> songs = songService.searchSongs(keyword, page, size, currentUser);
            return ResponseEntity.ok(ApiResponse.success("Songs found", songs));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Error searching songs: " + e.getMessage()));
        }
    }
    @GetMapping("/public/latest-suggestions")
    public ResponseEntity<?> getTop5LatestSongs() {
        try {
            List<SongResponse> songs = songService.getTop5LatestSongs();
            return ResponseEntity.ok(ApiResponse.success("Top 5 latest songs", songs));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Error retrieving latest songs: " + e.getMessage()));
        }
    }
    @GetMapping("/public/active")
    public ResponseEntity<?> getAllActiveSongs() {
        try {
            List<SongResponse> songs = songService.getAllActiveSongs();
            return ResponseEntity.ok(ApiResponse.success("Active songs retrieved successfully", songs));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Error retrieving active songs: " + e.getMessage()));
        }
    }

    @GetMapping("/genre/{genreName}")
    public ResponseEntity<?> getSongsByGenre(
            @PathVariable String genreName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        try {
            Page<SongResponse> songs = songService.getSongsByGenre(genreName, page, size, currentUser);
            return ResponseEntity.ok(ApiResponse.success("Songs by genre retrieved successfully", songs));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Error retrieving songs by genre: " + e.getMessage()));
        }
    }
    @GetMapping("/public/top5-playcount")
    public ResponseEntity<?> getTop5PlayCountSongs() {
        try {
            List<SongResponse> songs = songService.getTop5PlayCountSongs();
            return ResponseEntity.ok(ApiResponse.success("Top 5 songs by play count retrieved", songs));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Error retrieving top 5 songs: " + e.getMessage()));
        }
    }

    @GetMapping("/public/find-by-id")
    public ResponseEntity<?> findById(@RequestParam("id") Long id,
                                      @AuthenticationPrincipal UserPrincipal currentUser) {
        try {
            SongResponse song = songService.getSongById(id, currentUser); // hoặc 1 hàm read-only
            return ResponseEntity.ok(ApiResponse.success("Song retrieved", song));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Song not found: " + e.getMessage()));
        }
    }
    @GetMapping(value = "/by-album/{albumId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getSongsByAlbum(
            @PathVariable("albumId") Long albumId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        try {
            Page<SongResponse> songs = songService.getSongsByAlbumId(albumId, page, size, currentUser);
            return ResponseEntity.ok(ApiResponse.success("Songs by album retrieved successfully", songs));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Error retrieving songs: " + e.getMessage()));
        }
    }

    // (tuỳ chọn) public không cần đăng nhập
    @GetMapping(value = "/public/by-album/{albumId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getSongsByAlbumPublic(
            @PathVariable("albumId") Long albumId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<SongResponse> songs = songService.getSongsByAlbumId(albumId, page, size, null);
        return ResponseEntity.ok(ApiResponse.success("Songs by album retrieved successfully", songs));
    }

    // Public endpoints (không cần authentication)
    @GetMapping("/public/latest")
    public ResponseEntity<?> getLatestSongsPublic(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            Page<SongResponse> songs = songService.getLatestSongs(page, size, null);
            return ResponseEntity.ok(ApiResponse.success("Latest songs retrieved successfully", songs));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Error retrieving songs: " + e.getMessage()));
        }
    }
    @GetMapping("/{id}")
    public ResponseEntity<?> getSongById(@PathVariable Long id,
                                         @AuthenticationPrincipal UserPrincipal currentUser) {
        try {
            SongResponse song = songService.getSongById(id, currentUser); // lấy trước

            try { songService.increasePlayCount(id); } catch (Exception ignored) {}

            if (currentUser != null) {
                try { songService.addPlayHistory(id, currentUser.getId()); } catch (Exception ignored) {}
            }

            return ResponseEntity.ok(ApiResponse.success("Song retrieved successfully", song));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Song not found: " + e.getMessage()));
        }
    }

    @PostMapping(value = "", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createSong(
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("lyrics") String lyrics,
            @RequestParam("duration") int duration,
            @RequestParam("genre.id") Long genreId,
            @RequestParam("album.id") Long albumId,
            @RequestParam("audioFile") MultipartFile audioFile,
            @RequestParam(value = "coverImage", required = false) MultipartFile coverImage,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        try {
            String uploadDir = "D:/web nhac/duan1/upload";
            Path dir = Paths.get(uploadDir);
            Files.createDirectories(dir);

            // 🎵 Lưu file nhạc
            String audioFileName = resolveUniqueFileName(dir, sanitizeFileName(audioFile.getOriginalFilename()));
            Path audioPath = dir.resolve(audioFileName);
            Files.copy(audioFile.getInputStream(), audioPath, StandardCopyOption.REPLACE_EXISTING);

            // 🎼 Tạo song mới
            Song song = new Song();
            song.setTitle(title);
            song.setDescription(description);
            song.setLyrics(lyrics);
            song.setDuration(duration);
            song.setFilePath("/upload/" + audioFileName); // đường dẫn FE dùng

            // 🖼️ Lưu ảnh bìa (nếu có)
            if (coverImage != null && !coverImage.isEmpty()) {
                String coverFileName = resolveUniqueFileName(dir, sanitizeFileName(coverImage.getOriginalFilename()));
                Path coverPath = dir.resolve(coverFileName);
                Files.copy(coverImage.getInputStream(), coverPath, StandardCopyOption.REPLACE_EXISTING);
                song.setCoverImage("/upload/" + coverFileName);
            }

            // 📌 Gán genre và album
            Genre genre = new Genre();
            genre.setId(genreId);
            song.setGenre(genre);

            Album album = new Album();
            album.setId(albumId);
            song.setAlbum(album);

            // ✅ Lưu song
            SongResponse created = songService.createSong(song, currentUser);
            return ResponseEntity.ok(ApiResponse.success("Song created successfully", created));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Error creating song: " + e.getMessage()));
        }
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateSong(
            @PathVariable Long id,
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("lyrics") String lyrics,
            @RequestParam("duration") int duration,
            @RequestParam("genre.id") Long genreId,
            @RequestParam("album.id") Long albumId,
            @RequestParam(value = "audioFile", required = false) MultipartFile audioFile,
            @RequestParam(value = "coverImage", required = false) MultipartFile coverImage,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        try {
            String uploadDir = "D:/web nhac/duan1/upload";
            Path dir = Paths.get(uploadDir);
            Files.createDirectories(dir);

            // 🔎 Lấy bài hát hiện tại
            Song song = songService.getByIdOrThrow(id);

            // 🔐 Kiểm tra quyền cập nhật
            if (!song.getArtist().getId().equals(currentUser.getId())) {
                return ResponseEntity.status(403).body(ApiResponse.error("You are not authorized to update this song"));
            }

            // 🎵 Nếu có file nhạc mới
            if (audioFile != null && !audioFile.isEmpty()) {
                String audioFileName = resolveUniqueFileName(dir, sanitizeFileName(audioFile.getOriginalFilename()));
                Path audioPath = dir.resolve(audioFileName);
                Files.copy(audioFile.getInputStream(), audioPath, StandardCopyOption.REPLACE_EXISTING);
                song.setFilePath("/upload/" + audioFileName);
            }

            // 🖼️ Nếu có ảnh bìa mới
            if (coverImage != null && !coverImage.isEmpty()) {
                String coverFileName = resolveUniqueFileName(dir, sanitizeFileName(coverImage.getOriginalFilename()));
                Path coverPath = dir.resolve(coverFileName);
                Files.copy(coverImage.getInputStream(), coverPath, StandardCopyOption.REPLACE_EXISTING);
                song.setCoverImage("/upload/" + coverFileName);
            }

            // 📝 Cập nhật các thông tin còn lại
            song.setTitle(title);
            song.setDescription(description);
            song.setLyrics(lyrics);
            song.setDuration(duration);

            Genre genre = new Genre();
            genre.setId(genreId);
            song.setGenre(genre);

            Album album = new Album();
            album.setId(albumId);
            song.setAlbum(album);

            // 💾 Lưu lại
            Song updated = songService.save(song);

            // 🔄 Trả về kết quả
            SongResponse response = songService.convertToSongResponse(updated, currentUser);
            return ResponseEntity.ok(ApiResponse.success("Song updated successfully", response));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Error updating song: " + e.getMessage()));
        }
    }
    private String sanitizeFileName(String original) {
        if (original == null || original.isBlank()) return "file";
        int dot = original.lastIndexOf('.');
        String name = (dot > 0) ? original.substring(0, dot) : original;
        String ext  = (dot > 0) ? original.substring(dot) : "";

        // bỏ ký tự lạ, thay khoảng trắng -> '-', gộp nhiều '-' liên tiếp
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
    public ResponseEntity<?> deleteSong(@PathVariable Long id, @AuthenticationPrincipal UserPrincipal currentUser) {
        try {
            songService.deleteSong(id, currentUser);
            return ResponseEntity.ok(ApiResponse.success("Song deleted successfully", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Error deleting song: " + e.getMessage()));
        }
    }

    @GetMapping("/public/popular")
    public ResponseEntity<?> getPopularSongsPublic(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            Page<SongResponse> songs = songService.getPopularSongs(page, size, null);
            return ResponseEntity.ok(ApiResponse.success("Popular songs retrieved successfully", songs));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Error retrieving popular songs: " + e.getMessage()));
        }
    }

}
