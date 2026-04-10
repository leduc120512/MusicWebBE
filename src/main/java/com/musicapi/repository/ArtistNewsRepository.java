package com.musicapi.repository;

import com.musicapi.model.ArtistNews;
import com.musicapi.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ArtistNewsRepository extends JpaRepository<ArtistNews, Long> {
    Page<ArtistNews> findByArtistAndPublishedTrueOrderByCreatedAtDesc(User artist, Pageable pageable);

    Page<ArtistNews> findByArtistOrderByCreatedAtDesc(User artist, Pageable pageable);

    Optional<ArtistNews> findByIdAndArtistAndPublishedTrue(Long id, User artist);

    Optional<ArtistNews> findByIdAndArtist(Long id, User artist);
}

