package com.musicapi.repository;

import com.musicapi.model.Playlist;
import com.musicapi.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlaylistRepository extends JpaRepository<Playlist, Long> {
    List<Playlist> findByUser(User user);
    Page<Playlist> findByUser(User user, Pageable pageable);
    
    @Query("SELECT p FROM Playlist p WHERE p.isPublic = true")
    Page<Playlist> findPublicPlaylists(Pageable pageable);
    
    @Query("SELECT p FROM Playlist p WHERE p.name LIKE %:keyword% AND p.isPublic = true")
    Page<Playlist> searchPublicPlaylists(@Param("keyword") String keyword, Pageable pageable);
}
