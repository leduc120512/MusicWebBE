package com.musicapi.service;

import com.musicapi.dto.FavoriteAlbumRequest;
import com.musicapi.dto.FavoriteAlbumResponse;
import com.musicapi.dto.SongResponse;
import com.musicapi.model.Playlist;
import com.musicapi.model.Song;
import com.musicapi.model.User;
import com.musicapi.repository.PlaylistRepository;
import com.musicapi.repository.SongRepository;
import com.musicapi.repository.UserRepository;
import com.musicapi.security.UserPrincipal;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
public class FavoriteAlbumService {
    @Autowired
    private PlaylistRepository playlistRepository;

    @Autowired
    private SongRepository songRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SongService songService;

    @Transactional
    public List<FavoriteAlbumResponse> getMyFavoriteAlbums(UserPrincipal currentUser) {
        requireLogin(currentUser);
        User user = getUser(currentUser.getId());
        return playlistRepository.findByUser(user).stream()
                .sorted(Comparator.comparing(Playlist::getCreatedAt, Comparator.nullsLast(Comparator.reverseOrder())))
                .map(playlist -> toResponse(playlist, currentUser, false))
                .toList();
    }

    @Transactional
    public FavoriteAlbumResponse getMyFavoriteAlbum(Long albumId, UserPrincipal currentUser) {
        Playlist playlist = getOwnedPlaylist(albumId, currentUser);
        return toResponse(playlist, currentUser, true);
    }

    public FavoriteAlbumResponse create(FavoriteAlbumRequest request, UserPrincipal currentUser) {
        requireLogin(currentUser);
        String name = request == null ? null : request.getName();
        if (name == null || name.trim().isEmpty()) {
            throw new RuntimeException("Favorite album name must not be blank");
        }

        Playlist playlist = new Playlist(name.trim(), getUser(currentUser.getId()));
        playlist.setDescription(request.getDescription());
        playlist.setPublic(request.getIsPublic() == null || request.getIsPublic());
        return toResponse(playlistRepository.save(playlist), currentUser, true);
    }

    @Transactional
    public FavoriteAlbumResponse update(Long albumId, FavoriteAlbumRequest request, UserPrincipal currentUser) {
        Playlist playlist = getOwnedPlaylist(albumId, currentUser);
        if (request.getName() != null && !request.getName().trim().isEmpty()) {
            playlist.setName(request.getName().trim());
        }
        if (request.getDescription() != null) {
            playlist.setDescription(request.getDescription());
        }
        if (request.getIsPublic() != null) {
            playlist.setPublic(request.getIsPublic());
        }
        return toResponse(playlistRepository.save(playlist), currentUser, true);
    }

    @Transactional
    public FavoriteAlbumResponse addSong(Long albumId, Long songId, UserPrincipal currentUser) {
        Playlist playlist = getOwnedPlaylist(albumId, currentUser);
        Song song = songRepository.findById(songId)
                .orElseThrow(() -> new RuntimeException("Song not found"));
        if (!song.isActive()) {
            throw new RuntimeException("Song is not active");
        }
        playlist.getSongs().add(song);
        return toResponse(playlistRepository.save(playlist), currentUser, true);
    }

    @Transactional
    public FavoriteAlbumResponse removeSong(Long albumId, Long songId, UserPrincipal currentUser) {
        Playlist playlist = getOwnedPlaylist(albumId, currentUser);
        playlist.getSongs().removeIf(song -> song.getId().equals(songId));
        return toResponse(playlistRepository.save(playlist), currentUser, true);
    }

    public void delete(Long albumId, UserPrincipal currentUser) {
        Playlist playlist = getOwnedPlaylist(albumId, currentUser);
        playlistRepository.delete(playlist);
    }

    private Playlist getOwnedPlaylist(Long albumId, UserPrincipal currentUser) {
        requireLogin(currentUser);
        return playlistRepository.findByIdAndUser_Id(albumId, currentUser.getId())
                .orElseThrow(() -> new RuntimeException("Favorite album not found"));
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private void requireLogin(UserPrincipal currentUser) {
        if (currentUser == null) {
            throw new RuntimeException("Unauthorized");
        }
    }

    private FavoriteAlbumResponse toResponse(Playlist playlist, UserPrincipal currentUser, boolean includeSongs) {
        FavoriteAlbumResponse response = new FavoriteAlbumResponse();
        response.setId(playlist.getId());
        response.setName(playlist.getName());
        response.setDescription(playlist.getDescription());
        response.setCoverImage(playlist.getCoverImage());
        response.setPublic(playlist.isPublic());
        response.setCreatedAt(playlist.getCreatedAt());
        response.setUpdatedAt(playlist.getUpdatedAt());
        response.setSongCount(playlist.getSongs().size());

        if (includeSongs) {
            List<SongResponse> songs = playlist.getSongs().stream()
                    .sorted(Comparator.comparing(Song::getCreatedAt, Comparator.nullsLast(Comparator.reverseOrder())))
                    .map(song -> songService.convertToSongResponse(song, currentUser))
                    .toList();
            response.setSongs(songs);
        }
        return response;
    }
}
