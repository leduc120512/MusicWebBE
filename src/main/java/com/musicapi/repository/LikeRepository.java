package com.musicapi.repository;

import com.musicapi.model.Like;
import com.musicapi.model.Song;
import com.musicapi.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {

    // Find a like by user and song
    Optional<Like> findByUserAndSong(User user, Song song);

    // Whether the like exists, matched by entity
    boolean existsByUserAndSong(User user, Song song);

    // Whether the like exists, matched by id
    boolean existsByUser_IdAndSong_Id(Long userId, Long songId);

    void deleteByUserAndSong(User user, Song song);

    // Songs liked by the given user
    @Query("SELECT l.song FROM Like l WHERE l.user = :user ORDER BY l.createdAt DESC")
    Page<Song> findLikedSongsByUser(@Param("user") User user, Pageable pageable);

    @Query("SELECT l.song FROM Like l WHERE l.user.id = :userId ORDER BY l.createdAt DESC")
    Page<Song> findLikedSongsByUserId(@Param("userId") Long userId, Pageable pageable);

    // Count likes for a song
    @Query("SELECT COUNT(l) FROM Like l WHERE l.song = :song")
    Long countLikesBySong(@Param("song") Song song);

    Long countBySong_Id(Long songId);

    Long countBySong_Artist_Id(Long artistId);
}
