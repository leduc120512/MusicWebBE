package com.musicapi.service;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import com.musicapi.model.Genre;
import com.musicapi.repository.GenreRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GenreService {

    private final GenreRepository genreRepository;

    public GenreService(GenreRepository genreRepository) {
        this.genreRepository = genreRepository;
    }

    /* ================= CREATE ================= */
    public Genre createGenre(Genre genre) {
        if (genreRepository.existsByNameIgnoreCase(genre.getName())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Genre already exists");
        }
        return genreRepository.save(genre);
    }

    /* ================= UPDATE ================= */
    public Genre updateGenre(Long id, Genre updated) {
        Genre genre = genreRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Genre not found"));

        genre.setName(updated.getName());
        genre.setDescription(updated.getDescription());

        return genreRepository.save(genre);
    }

    /* ================= DELETE ================= */
    public void deleteGenre(Long id) {
        Genre genre = genreRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Genre not found"));

        // enable to block deletion while songs still reference it
        if (!genre.getSongs().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Cannot delete genre with existing songs");
        }

        genreRepository.delete(genre);
    }

    /* ================= GET ================= */
    public Genre getById(Long id) {
        return genreRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Genre not found"));
    }

    public List<Genre> getAllGenres() {
        return genreRepository.findAll();
    }
}
