package com.musicapi.service;



import com.musicapi.model.Genre;
import com.musicapi.repository.GenreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GenreService {

    @Autowired
    private GenreRepository genreRepository;

    /* ================= CREATE ================= */
    public Genre createGenre(Genre genre) {
        if (genreRepository.existsByNameIgnoreCase(genre.getName())) {
            throw new RuntimeException("Genre already exists");
        }
        return genreRepository.save(genre);
    }

    /* ================= UPDATE ================= */
    public Genre updateGenre(Long id, Genre updated) {
        Genre genre = genreRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Genre not found"));

        genre.setName(updated.getName());
        genre.setDescription(updated.getDescription());

        return genreRepository.save(genre);
    }

    /* ================= DELETE ================= */
    public void deleteGenre(Long id) {
        Genre genre = genreRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Genre not found"));

        // ⚠️ nếu muốn chặn xóa khi còn bài hát
        if (!genre.getSongs().isEmpty()) {
            throw new RuntimeException("Cannot delete genre with existing songs");
        }

        genreRepository.delete(genre);
    }

    /* ================= GET ================= */
    public Genre getById(Long id) {
        return genreRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Genre not found"));
    }

    public List<Genre> getAllGenres() {
        return genreRepository.findAll();
    }
}
