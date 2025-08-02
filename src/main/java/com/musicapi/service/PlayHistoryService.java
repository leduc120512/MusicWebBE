package com.musicapi.service;

import com.musicapi.model.PlayHistory;
import com.musicapi.model.Song;
import com.musicapi.model.User;
import com.musicapi.repository.PlayHistoryRepository;
import com.musicapi.repository.SongRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

import java.time.LocalDateTime;

@Service
public class PlayHistoryService {

    @Autowired
    private PlayHistoryRepository playHistoryRepository;

    @Autowired
    private SongRepository songRepository;

    /**
     * Ghi lại lịch sử và tăng playCount
     */
    @Transactional
    public void logPlayAndIncrementCount(User user, Song song) {
        // Ghi lịch sử nghe nhạc
        PlayHistory history = new PlayHistory(user, song);
        playHistoryRepository.save(history);

        // Tăng playCount
        song.setPlayCount(song.getPlayCount() + 1);
        songRepository.save(song);
    }


        public void deleteAllByUser(User user) {
            playHistoryRepository.deleteByUser(user);
        }

        public void deleteByUserAndSong(User user, Song song) {
            playHistoryRepository.deleteByUserAndSong(user, song);
        }
    public void updatePlayHistory(Long historyId, Song newSong, LocalDateTime newPlayedAt) {
        PlayHistory history = playHistoryRepository.findById(historyId)
                .orElseThrow(() -> new RuntimeException("Play history not found"));

        if (newSong != null) {
            history.setSong(newSong);
        }
        if (newPlayedAt != null) {
            history.setPlayedAt(newPlayedAt);
        }

        playHistoryRepository.save(history);
    }

}
