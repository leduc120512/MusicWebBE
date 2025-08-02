package com.musicapi.repository;

import com.musicapi.model.PlayHistory;
import com.musicapi.model.Song;
import com.musicapi.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jakarta.transaction.Transactional;

@Repository
public interface PlayHistoryRepository extends JpaRepository<PlayHistory, Long> {

    @Query("SELECT ph.song FROM PlayHistory ph WHERE ph.user = :user ORDER BY ph.playedAt DESC")
    Page<Song> findRecentlyPlayedByUser(@Param("user") User user, Pageable pageable);

    @Query("SELECT COUNT(ph) FROM PlayHistory ph WHERE ph.song = :song")
    Long countPlaysBySong(@Param("song") Song song);

    @Query("SELECT COUNT(ph) FROM PlayHistory ph WHERE ph.user = :user")
    Long countPlaysByUser(@Param("user") User user);

    // 🆕 Thêm để hỗ trợ DELETE
    @Transactional
    void deleteByUser(User user);

    @Transactional
    void deleteByUserAndSong(User user, Song song);
}
