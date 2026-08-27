package com.musicapi.repository;

import com.musicapi.model.Song;
import com.musicapi.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SongRepository extends JpaRepository<Song, Long> {
    List<Song> findByArtist(User artist);
    Page<Song> findByArtist(User artist, Pageable pageable);
    Long countByArtist(User artist);
    Long countByArtist_Id(Long artistId);

    @Query("SELECT COALESCE(SUM(s.playCount), 0) FROM Song s WHERE s.artist = :artist")
    Long sumPlayCountByArtist(@Param("artist") User artist);

    @Query("SELECT COALESCE(SUM(s.playCount), 0) FROM Song s WHERE s.artist.id = :artistId")
    Long sumPlayCountByArtistId(@Param("artistId") Long artistId);

    @Query("SELECT COALESCE(SUM(s.playCount), 0) FROM Song s")
    Long sumAllPlayCount();

    @Query("SELECT s FROM Song s WHERE s.active = true")
    Page<Song> findActiveSongs(Pageable pageable);

    @Query("SELECT s FROM Song s WHERE s.title LIKE %:keyword% AND s.active = true")
    Page<Song> searchByTitle(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT s FROM Song s WHERE s.artist.fullName LIKE %:keyword% AND s.active = true")
    Page<Song> searchByArtistName(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT s FROM Song s WHERE s.genre.name = :genreName AND s.active = true")
    Page<Song> findByGenreName(@Param("genreName") String genreName, Pageable pageable);

    @Query("SELECT s FROM Song s WHERE s.album.id = :albumId AND s.active = true ORDER BY s.createdAt DESC")
    Page<Song> findByAlbumId(@Param("albumId") Long albumId, Pageable pageable);

    @Query("SELECT s FROM Song s WHERE s.active = true ORDER BY s.playCount DESC")
    Page<Song> findPopularSongs(Pageable pageable);

    @Query("SELECT s FROM Song s WHERE s.active = true ORDER BY s.playCount DESC, s.createdAt DESC")
    Page<Song> findTrendingSongs(Pageable pageable);

    @Query("SELECT s FROM Song s WHERE s.active = true ORDER BY s.createdAt DESC")
    Page<Song> findLatestSongs(Pageable pageable);
    @Query("SELECT s FROM Song s WHERE (s.title LIKE %:keyword% OR s.description LIKE %:keyword%) AND s.active = true ORDER BY s.playCount DESC")
    List<Song> findTop5ByKeyword(@Param("keyword") String keyword);
    @Query("SELECT s FROM Song s WHERE s.active = true ORDER BY s.createdAt DESC")
    List<Song> findTop5LatestSongs(Pageable pageable);
    @Query("SELECT s FROM Song s WHERE s.top = true")
    List<Song> findAllActiveSongs();
    @Query("SELECT s FROM Song s WHERE s.active = true ORDER BY s.playCount DESC")
    List<Song> findTop5ByPlayCount(Pageable pageable);


}
