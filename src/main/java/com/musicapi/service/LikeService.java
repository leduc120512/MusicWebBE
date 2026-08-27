package com.musicapi.service;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import com.musicapi.dto.SongResponse;
import com.musicapi.model.Like;
import com.musicapi.model.Song;
import com.musicapi.model.User;
import com.musicapi.repository.LikeRepository;
import com.musicapi.repository.SongRepository;
import com.musicapi.repository.UserRepository;
import com.musicapi.security.UserPrincipal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LikeService {

    private final LikeRepository likeRepository;
    private final SongRepository songRepository;
    private final UserRepository userRepository;
    private final SongService songService;

    public LikeService(
            LikeRepository likeRepository,
            SongRepository songRepository,
            UserRepository userRepository,
            SongService songService
    ) {
        this.likeRepository = likeRepository;
        this.songRepository = songRepository;
        this.userRepository = userRepository;
        this.songService = songService;
    }

    @Transactional
    public boolean likeSong(Long songId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        Song song = songRepository.findById(songId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Song not found"));

        if (likeRepository.existsByUserAndSong(user, song)) {
            return false;
        }

        likeRepository.save(new Like(user, song));
        return true;
    }

    @Transactional
    public boolean unlikeSong(Long songId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        Song song = songRepository.findById(songId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Song not found"));

        if (!likeRepository.existsByUserAndSong(user, song)) {
            return false;
        }

        likeRepository.deleteByUserAndSong(user, song);
        return true;
    }

    public boolean isLiked(Long songId, Long userId) {
        return likeRepository.existsByUser_IdAndSong_Id(userId, songId);
    }

    public Long countLikes(Long songId) {
        return likeRepository.countBySong_Id(songId);
    }

    @Transactional(readOnly = true)
    public Page<SongResponse> getLikedSongs(UserPrincipal currentUser, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Song> songs = likeRepository.findLikedSongsByUserId(currentUser.getId(), pageable);
        return songs.map(song -> songService.convertToSongResponse(song, currentUser));
    }
}

