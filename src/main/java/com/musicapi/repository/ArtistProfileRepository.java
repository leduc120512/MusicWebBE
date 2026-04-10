package com.musicapi.repository;

import com.musicapi.model.ArtistProfile;
import com.musicapi.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ArtistProfileRepository extends JpaRepository<ArtistProfile, Long> {
    Optional<ArtistProfile> findByArtist(User artist);
    Optional<ArtistProfile> findByArtistId(Long artistId);
}

