package com.musicapi.controller;



import com.musicapi.dto.ApiResponse;
import com.musicapi.model.Genre;
import com.musicapi.service.GenreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/genres")
@CrossOrigin(origins = "*")
public class GenreController {

    @Autowired
    private GenreService genreService;

    /* ================= CREATE ================= */
    @PostMapping
    public ResponseEntity<?> createGenre(@RequestBody Genre genre) {
        try {
            Genre created = genreService.createGenre(genre);
            return ResponseEntity.ok(ApiResponse.success("Genre created successfully", created));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /* ================= UPDATE ================= */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateGenre(
            @PathVariable Long id,
            @RequestBody Genre genre
    ) {
        try {
            Genre updated = genreService.updateGenre(id, genre);
            return ResponseEntity.ok(ApiResponse.success("Genre updated successfully", updated));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /* ================= DELETE ================= */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteGenre(@PathVariable Long id) {
        try {
            genreService.deleteGenre(id);
            return ResponseEntity.ok(ApiResponse.success("Genre deleted successfully", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /* ================= GET ================= */
    @GetMapping("/{id}")
    public ResponseEntity<?> getGenreById(@PathVariable Long id) {
        try {
            Genre genre = genreService.getById(id);
            return ResponseEntity.ok(ApiResponse.success("Genre retrieved", genre));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /* ================= PUBLIC ================= */
    @GetMapping
    public ResponseEntity<?> getAllGenresAtRoot() {
        try {
            List<Genre> genres = genreService.getAllGenres();
            return ResponseEntity.ok(ApiResponse.success("Genres retrieved", genres));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/public")
    public ResponseEntity<?> getAllGenres() {
        try {
            List<Genre> genres = genreService.getAllGenres();
            return ResponseEntity.ok(ApiResponse.success("Genres retrieved", genres));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
}
