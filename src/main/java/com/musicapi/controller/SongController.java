package com.musicapi.controller;

import com.musicapi.dto.ApiResponse;
import com.musicapi.service.FileStorageService;
import com.musicapi.dto.SongLyricsUpdateRequest;
import com.musicapi.dto.SongResponse;
import com.musicapi.model.Album;
import com.musicapi.model.Genre;
import com.musicapi.model.Song;
import com.musicapi.security.UserPrincipal;
import com.musicapi.service.SongService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.multipart.MultipartFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@RestController
@RequestMapping("/api/songs")
@CrossOrigin(origins = "*")@Tag(name = "Songs", description = "Song catalogue, search and uploads")
public class SongController {

    private final SongService songService;

    private final FileStorageService fileStorageService;

    public SongController(SongService songService, FileStorageService fileStorageService) {
        this.songService = songService;
        this.fileStorageService = fileStorageService;
    }


    @GetMapping("/latest")
    public ResponseEntity<?> getLatestSongs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        Page<SongResponse> songs = songService.getLatestSongs(page, size, currentUser);
        return ResponseEntity.ok(ApiResponse.success("Latest songs retrieved successfully", songs));
    }

    @GetMapping("/popular")
    public ResponseEntity<?> getPopularSongs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        Page<SongResponse> songs = songService.getPopularSongs(page, size, currentUser);
        return ResponseEntity.ok(ApiResponse.success("Popular songs retrieved successfully", songs));
    }

    @GetMapping("/trending")
    public ResponseEntity<?> getTrendingSongs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        Page<SongResponse> songs = songService.getTrendingSongs(page, size, currentUser);
        return ResponseEntity.ok(ApiResponse.success("Trending songs retrieved successfully", songs));
    }

    @GetMapping({"/me", "/my"})
    public ResponseEntity<?> getMySongs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        Page<SongResponse> songs = songService.getMySongs(currentUser, page, size);
        return ResponseEntity.ok(ApiResponse.success("My songs retrieved successfully", songs));
    }

    @GetMapping("/public/search-suggestions")
    public ResponseEntity<?> getSearchSuggestions(@RequestParam(required = false) String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Missing keyword parameter"));
        }

        List<SongResponse> suggestions = songService.searchTopSongs(keyword);
        return ResponseEntity.ok(ApiResponse.success("Suggestions retrieved", suggestions));
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchSongs(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        Page<SongResponse> songs = songService.searchSongs(keyword, page, size, currentUser);
        return ResponseEntity.ok(ApiResponse.success("Songs found", songs));
    }
    @GetMapping("/public/latest-suggestions")
    public ResponseEntity<?> getTop5LatestSongs() {
        List<SongResponse> songs = songService.getTop5LatestSongs();
        return ResponseEntity.ok(ApiResponse.success("Top 5 latest songs", songs));
    }
    /**
     * Paginated song catalogue. Newest first, so an empty database still returns
     * a well-formed page rather than an error.
     */
    @GetMapping
    @Operation(summary = "Browse the song catalogue, newest first")
    public ResponseEntity<?> getSongs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        Page<SongResponse> songs = songService.getLatestSongs(page, size, currentUser);
        return ResponseEntity.ok(ApiResponse.success("Songs retrieved successfully", songs));
    }
    @GetMapping("/public/active")
    public ResponseEntity<?> getAllActiveSongs() {
        List<SongResponse> songs = songService.getAllActiveSongs();
        return ResponseEntity.ok(ApiResponse.success("Active songs retrieved successfully", songs));
    }

    @GetMapping("/genre/{genreName}")
    public ResponseEntity<?> getSongsByGenre(
            @PathVariable String genreName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        Page<SongResponse> songs = songService.getSongsByGenre(genreName, page, size, currentUser);
        return ResponseEntity.ok(ApiResponse.success("Songs by genre retrieved successfully", songs));
    }

    @GetMapping("/by-album/{albumId}")
    public ResponseEntity<?> getSongsByAlbum(
            @PathVariable Long albumId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        Page<SongResponse> songs = songService.getSongsByAlbum(albumId, page, size, currentUser);
        return ResponseEntity.ok(ApiResponse.success("Songs by album retrieved successfully", songs));
    }

    @GetMapping("/public/top5-playcount")
    public ResponseEntity<?> getTop5PlayCountSongs() {
        List<SongResponse> songs = songService.getTop5PlayCountSongs();
        return ResponseEntity.ok(ApiResponse.success("Top 5 songs by play count retrieved", songs));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getSongById(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal currentUser // null when the caller is anonymous
    ) {
        // 1. always bump the play count
        songService.increasePlayCount(id);

        // 2. load the song
        SongResponse song = songService.getSongById(id, currentUser);

        // 3. record history only for signed-in callers
        if (currentUser != null) {
            songService.addPlayHistory(id, currentUser.getId());
        }

        return ResponseEntity.ok(ApiResponse.success("Song retrieved successfully", song));
    }

    // Public endpoints (no authentication required)
    @GetMapping("/public/latest")
    public ResponseEntity<?> getLatestSongsPublic(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<SongResponse> songs = songService.getLatestSongs(page, size, null);
        return ResponseEntity.ok(ApiResponse.success("Latest songs retrieved successfully", songs));
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
        // build the new song
        Song song = new Song();
        song.setTitle(title);
        song.setDescription(description);
        song.setLyrics(lyrics);
        song.setDuration(duration);
        song.setFilePath(fileStorageService.store(audioFile));

        String coverPath = fileStorageService.store(coverImage);
        if (coverPath != null) {
            song.setCoverImage(coverPath);
        }

        // attach genre and album
        Genre genre = new Genre();
        genre.setId(genreId);
        song.setGenre(genre);

        Album album = new Album();
        album.setId(albumId);
        song.setAlbum(album);

        // persist the song
        SongResponse created = songService.createSong(song, currentUser);
        return ResponseEntity.ok(ApiResponse.success("Song created successfully", created));
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
        // ownership is enforced by SongService
        Song song = songService.getByIdOrThrow(id);

        String newAudioPath = fileStorageService.store(audioFile);
        if (newAudioPath != null) {
            song.setFilePath(newAudioPath);
        }

        String newCoverPath = fileStorageService.store(coverImage);
        if (newCoverPath != null) {
            song.setCoverImage(newCoverPath);
        }

        // copy the remaining fields
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

        // persist
        Song updated = songService.save(song);

        // build the response
        SongResponse response = songService.convertToSongResponse(updated, currentUser);
        return ResponseEntity.ok(ApiResponse.success("Song updated successfully", response));
    }

    @PatchMapping("/{id}/lyrics")
    public ResponseEntity<?> updateLyrics(
            @PathVariable Long id,
            @jakarta.validation.Valid @RequestBody SongLyricsUpdateRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        SongResponse updated = songService.updateLyrics(id, request.getLyrics(), currentUser);
        return ResponseEntity.ok(ApiResponse.success("Lyrics updated successfully", updated));
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
        songService.deleteSong(id, currentUser);
        return ResponseEntity.ok(ApiResponse.success("Song deleted successfully", null));
    }

    @GetMapping("/public/popular")
    public ResponseEntity<?> getPopularSongsPublic(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<SongResponse> songs = songService.getPopularSongs(page, size, null);
        return ResponseEntity.ok(ApiResponse.success("Popular songs retrieved successfully", songs));
    }

    @GetMapping("/public/trending")
    public ResponseEntity<?> getTrendingSongsPublic(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<SongResponse> songs = songService.getTrendingSongs(page, size, null);
        return ResponseEntity.ok(ApiResponse.success("Trending songs retrieved successfully", songs));
    }

}
