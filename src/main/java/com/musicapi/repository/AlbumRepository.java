package com.musicapi.repository;

import com.musicapi.model.Album;
import com.musicapi.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlbumRepository extends JpaRepository<Album, Long> {
    List<Album> findByArtist(User artist);
    Page<Album> findByArtist(User artist, Pageable pageable);
    
    @Query("SELECT a FROM Album a WHERE a.title LIKE %:keyword%")
    Page<Album> searchByTitle(@Param("keyword") String keyword, Pageable pageable);
    
    @Query("SELECT a FROM Album a WHERE a.artist.fullName LIKE %:keyword%")
    Page<Album> searchByArtistName(@Param("keyword") String keyword, Pageable pageable);
    
    @Query("SELECT a FROM Album a ORDER BY a.createdAt DESC")
    Page<Album> findLatestAlbums(Pageable pageable);
}
