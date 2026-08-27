package com.musicapi.service;

import com.musicapi.dto.SongResponse;
import com.musicapi.model.PlayHistory;
import com.musicapi.model.Song;
import com.musicapi.model.User;
import com.musicapi.repository.PlayHistoryRepository;
import com.musicapi.repository.SongRepository;
import com.musicapi.repository.UserRepository;
import com.musicapi.security.UserPrincipal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

/**
 * Listening history: what a user played, how often, and the maintenance
 * operations on those rows.
 */
@Service
public class PlayHistoryService {

    private final PlayHistoryRepository playHistoryRepository;
    private final SongRepository songRepository;
    private final UserRepository userRepository;
    private final SongService songService;

    public PlayHistoryService(
            PlayHistoryRepository playHistoryRepository,
            SongRepository songRepository,
            UserRepository userRepository,
            SongService songService
    ) {
        this.playHistoryRepository = playHistoryRepository;
        this.songRepository = songRepository;
        this.userRepository = userRepository;
        this.songService = songService;
    }

    /** Appends a history row and bumps the song's play counter. */
    @Transactional
    public void logPlayAndIncrementCount(User user, Song song) {
        playHistoryRepository.save(new PlayHistory(user, song));

        song.setPlayCount(song.getPlayCount() + 1);
        songRepository.save(song);
    }

    @Transactional(readOnly = true)
    public Page<SongResponse> getRecentlyPlayed(UserPrincipal currentUser, int page, int size) {
        User user = requireUser(currentUser.getId());
        Page<Song> songs = playHistoryRepository.findRecentlyPlayedByUser(user, PageRequest.of(page, size));
        return songService.convertToSongResponsePage(songs, currentUser);
    }

    public Long countPlaysBySong(Long songId) {
        return playHistoryRepository.countPlaysBySong(requireSong(songId));
    }

    public Long countPlaysByUser(Long userId) {
        return playHistoryRepository.countPlaysByUser(requireUser(userId));
    }

    @Transactional
    public void clearHistory(Long userId) {
        playHistoryRepository.deleteByUser(requireUser(userId));
    }

    @Transactional
    public void removeSong(Long userId, Long songId) {
        playHistoryRepository.deleteByUserAndSong(requireUser(userId), requireSong(songId));
    }

    @Transactional
    public void updatePlayHistory(Long historyId, Long newSongId, LocalDateTime newPlayedAt) {
        PlayHistory history = playHistoryRepository.findById(historyId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Play history entry not found"));

        if (newSongId != null) {
            history.setSong(requireSong(newSongId));
        }
        if (newPlayedAt != null) {
            history.setPlayedAt(newPlayedAt);
        }

        playHistoryRepository.save(history);
    }

    private User requireUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }

    private Song requireSong(Long id) {
        return songRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Song not found"));
    }
}
