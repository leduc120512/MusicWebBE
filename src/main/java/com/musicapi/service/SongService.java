package com.musicapi.service;

import com.musicapi.dto.SongResponse;
import com.musicapi.model.PlayHistory;
import com.musicapi.model.Song;
import com.musicapi.model.User;
import com.musicapi.repository.LikeRepository;
import com.musicapi.repository.PlayHistoryRepository;
import com.musicapi.repository.SongRepository;
import com.musicapi.repository.UserRepository;
import com.musicapi.security.UserPrincipal;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SongService {

    @Autowired
    private SongRepository songRepository;
    @Autowired
    private PlayHistoryRepository playHistoryRepository;

    @Autowired
    private LikeRepository likeRepository;

    @Autowired
    private UserRepository userRepository;

    public Page<SongResponse> getLatestSongs(int page, int size, UserPrincipal currentUser) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Song> songs = songRepository.findLatestSongs(pageable);
        
        return songs.map(song -> convertToSongResponse(song, currentUser));
    }
    public SongResponse createSong(Song song, UserPrincipal currentUser) {
        User artist = userRepository.findById(currentUser.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        song.setArtist(artist);
        song.setActive(true);
        song.setTop(true);
        Song saved = songRepository.save(song);
        return convertToSongResponse(saved, currentUser);
    }
    public SongResponse updateSong(Long id, Song updatedSong, UserPrincipal currentUser) {
        Song song = songRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Song not found"));

        // Optionally check ownership:
        if (!song.getArtist().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Unauthorized to update this song");
        }

        song.setTitle(updatedSong.getTitle());
        song.setDescription(updatedSong.getDescription());
        song.setLyrics(updatedSong.getLyrics());
        song.setFilePath(updatedSong.getFilePath());
        song.setCoverImage(updatedSong.getCoverImage());
        song.setDuration(updatedSong.getDuration());
        song.setGenre(updatedSong.getGenre());
        song.setAlbum(updatedSong.getAlbum());

        Song saved = songRepository.save(song);
        return convertToSongResponse(saved, currentUser);
    }
    public void deleteSong(Long id, UserPrincipal currentUser) {
        Song song = songRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Song not found"));

        if (!song.getArtist().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Unauthorized to delete this song");
        }

        songRepository.delete(song);
    }

    public Page<SongResponse> getPopularSongs(int page, int size, UserPrincipal currentUser) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Song> songs = songRepository.findPopularSongs(pageable);
        
        return songs.map(song -> convertToSongResponse(song, currentUser));
    }

    public Page<SongResponse> searchSongs(String keyword, int page, int size, UserPrincipal currentUser) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Song> songs = songRepository.searchByTitle(keyword, pageable);
        
        return songs.map(song -> convertToSongResponse(song, currentUser));
    }
    public Song getByIdOrThrow(Long id) {
        return songRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Song not found"));
    }

    public Song save(Song song) {
        return songRepository.save(song);
    }

    public Page<SongResponse> getSongsByGenre(String genreName, int page, int size, UserPrincipal currentUser) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Song> songs = songRepository.findByGenreName(genreName, pageable);
        
        return songs.map(song -> convertToSongResponse(song, currentUser));
    }
    @Transactional
    public void addPlayHistory(Long songId, Long userId) {
        Song song = songRepository.findById(songId)
                .orElseThrow(() -> new RuntimeException("Song not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        PlayHistory history = new PlayHistory(user, song);
        playHistoryRepository.save(history);
    }
    public SongResponse getSongById(Long id, UserPrincipal currentUser) {
        Song song = songRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Song not found"));
        
        return convertToSongResponse(song, currentUser);
    }

    public  SongResponse convertToSongResponse(Song song, UserPrincipal currentUser) {
        SongResponse response = new SongResponse();
        response.setId(song.getId());
        response.setTitle(song.getTitle());
        response.setDescription(song.getDescription());
        response.setCoverImage(song.getCoverImage());
        response.setLyrics(song.getLyrics()); // ✅ sửa lỗi ghi đè ở đây

        response.setDuration(song.getDuration());
        response.setPlayCount(song.getPlayCount());
        response.setArtistName(song.getArtist().getFullName());
        response.setAlbumTitle(song.getAlbum() != null ? song.getAlbum().getTitle() : null);
        response.setGenreName(song.getGenre() != null ? song.getGenre().getName() : null);
        response.setCreatedAt(song.getCreatedAt());

        if (currentUser != null) {
            User user = userRepository.findById(currentUser.getId()).orElse(null);
            if (user != null) {
                response.setLiked(likeRepository.existsByUserAndSong(user, song));
            }
        }

        return response;
    }
    public List<SongResponse> getTop5LatestSongs() {
        Pageable limitFive = PageRequest.of(0, 5, Sort.by("createdAt").descending());
        List<Song> songs = songRepository.findTop5LatestSongs(limitFive);

        return songs.stream()
                .map(song -> convertToSongResponse(song, null))
                .toList();
    }
    public List<SongResponse> getAllActiveSongs() {
        List<Song> songs = songRepository.findAllActiveSongs();
        return songs.stream()
                .map(song -> convertToSongResponse(song, null))
                .toList();
    }

    public List<SongResponse> searchTopSongs(String keyword) {

        keyword = keyword.trim();

        List<Song> songs = songRepository.findTop5ByKeyword(keyword);
        return songs.stream()
                .map(song -> convertToSongResponse(song, null))
                .toList();
    }
    @Transactional
    public void increasePlayCount(Long songId) {
        songRepository.findById(songId).ifPresent(song -> {
            song.setPlayCount(song.getPlayCount() + 1);
            songRepository.save(song);
        });
    }

    public List<SongResponse> getTop5PlayCountSongs() {
        Pageable topFive = PageRequest.of(0, 5); // lấy 5 bài
        List<Song> songs = songRepository.findTop5ByPlayCount(topFive);

        return songs.stream()
                .map(song -> convertToSongResponse(song, null))
                .toList();
    }

    public Page<SongResponse> convertToSongResponsePage(Page<Song> songs, UserPrincipal currentUser) {
        return songs.map(song -> convertToSongResponse(song, currentUser));
    }

    public SongResponse convertToSongResponse(Song song) {
        return convertToSongResponse(song, null);
    }

}
