package com.musicapi.repository;

import com.musicapi.model.Playlist;
import com.musicapi.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlaylistRepository extends JpaRepository<Playlist, Long> {
    @EntityGraph(attributePaths = "songs")
    List<Playlist> findByUser(User user);
    Page<Playlist> findByUser(User user, Pageable pageable);

    @EntityGraph(attributePaths = "songs")
    Optional<Playlist> findByIdAndUser_Id(Long id, Long userId);
    
    @Query("SELECT p FROM Playlist p WHERE p.isPublic = true")
    Page<Playlist> findPublicPlaylists(Pageable pageable);
    
    @Query("SELECT p FROM Playlist p WHERE p.name LIKE %:keyword% AND p.isPublic = true")
    Page<Playlist> searchPublicPlaylists(@Param("keyword") String keyword, Pageable pageable);
}
