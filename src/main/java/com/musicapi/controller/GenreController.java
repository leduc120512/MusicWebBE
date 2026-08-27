package com.musicapi.controller;

import com.musicapi.dto.ApiResponse;
import com.musicapi.model.Genre;
import com.musicapi.service.GenreService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/genres")
@CrossOrigin(origins = "*")@Tag(name = "Genres", description = "Genre catalogue")
public class GenreController {

    private final GenreService genreService;

    public GenreController(GenreService genreService) {
        this.genreService = genreService;
    }

    /* ================= CREATE ================= */
    @PostMapping
    public ResponseEntity<?> createGenre(@RequestBody Genre genre) {
        Genre created = genreService.createGenre(genre);
        return ResponseEntity.ok(ApiResponse.success("Genre created successfully", created));
    }

    /* ================= UPDATE ================= */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateGenre(
            @PathVariable Long id,
            @RequestBody Genre genre
    ) {
        Genre updated = genreService.updateGenre(id, genre);
        return ResponseEntity.ok(ApiResponse.success("Genre updated successfully", updated));
    }

    /* ================= DELETE ================= */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteGenre(@PathVariable Long id) {
        genreService.deleteGenre(id);
        return ResponseEntity.ok(ApiResponse.success("Genre deleted successfully", null));
    }

    /* ================= GET ================= */
    @GetMapping("/{id}")
    public ResponseEntity<?> getGenreById(@PathVariable Long id) {
        Genre genre = genreService.getById(id);
        return ResponseEntity.ok(ApiResponse.success("Genre retrieved", genre));
    }

    /* ================= PUBLIC ================= */
    @GetMapping
    public ResponseEntity<?> getAllGenresAtRoot() {
        List<Genre> genres = genreService.getAllGenres();
        return ResponseEntity.ok(ApiResponse.success("Genres retrieved", genres));
    }

    @GetMapping("/public")
    public ResponseEntity<?> getAllGenres() {
        List<Genre> genres = genreService.getAllGenres();
        return ResponseEntity.ok(ApiResponse.success("Genres retrieved", genres));
    }
}
