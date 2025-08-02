package com.musicapi.service;

import com.musicapi.dto.AlbumResponse;
import com.musicapi.dto.SongResponse;
import com.musicapi.model.Album;
import com.musicapi.model.Song;
import com.musicapi.model.User;
import com.musicapi.repository.AlbumRepository;
import com.musicapi.repository.LikeRepository;
import com.musicapi.repository.UserRepository;
import com.musicapi.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AlbumService {

    @Autowired
    private AlbumRepository albumRepository;

    @Autowired
    private LikeRepository likeRepository;
    @Autowired
    private UserRepository userRepository;

    public Page<AlbumResponse> getLatestAlbums(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Album> albums = albumRepository.findLatestAlbums(pageable);
        
        return albums.map(this::convertToAlbumResponse);
    }

    public Page<AlbumResponse> searchAlbums(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Album> albums = albumRepository.searchByTitle(keyword, pageable);
        
        return albums.map(this::convertToAlbumResponse);
    }
    public Album createAlbum(Album album, UserPrincipal currentUser) {
        User artist = userRepository.findById(currentUser.getId())
                .orElseThrow(() -> new RuntimeException("Artist not found"));

        album.setId(null); // để tránh bị update nhầm nếu frontend gửi lên ID
        album.setArtist(artist);
        return albumRepository.save(album);
    }

    public Album updateAlbum(Long id, Album albumUpdate, UserPrincipal currentUser) {
        Album existing = albumRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Album not found"));

        if (!existing.getArtist().getId().equals(currentUser.getId())) {
            throw new RuntimeException("You are not allowed to update this album");
        }

        existing.setTitle(albumUpdate.getTitle());
        existing.setDescription(albumUpdate.getDescription());
        existing.setCoverImage(albumUpdate.getCoverImage());
        existing.setReleaseDate(albumUpdate.getReleaseDate());

        return albumRepository.save(existing);
    }

    public void deleteAlbum(Long id, UserPrincipal currentUser) {
        Album album = albumRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Album not found"));

        if (!album.getArtist().getId().equals(currentUser.getId())) {
            throw new RuntimeException("You are not allowed to delete this album");
        }

        albumRepository.delete(album);
    }
    public AlbumResponse getAlbumById(Long id, UserPrincipal currentUser) {
        Album album = albumRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Album not found"));
        
        AlbumResponse response = convertToAlbumResponse(album);
        
        // Convert songs with like status
        List<SongResponse> songResponses = album.getSongs().stream()
                .map(song -> convertToSongResponse(song, currentUser))
                .collect(Collectors.toList());
        
        response.setSongs(songResponses);
        return response;
    }

    private AlbumResponse convertToAlbumResponse(Album album) {
        AlbumResponse response = new AlbumResponse();
        response.setId(album.getId());
        response.setTitle(album.getTitle());
        response.setDescription(album.getDescription());
        response.setCoverImage(album.getCoverImage());
        response.setReleaseDate(album.getReleaseDate());
        response.setArtistName(album.getArtist().getFullName());
        response.setCreatedAt(album.getCreatedAt());
        response.setSongCount(album.getSongs().size());
        return response;
    }

    private SongResponse convertToSongResponse(Song song, UserPrincipal currentUser) {
        SongResponse response = new SongResponse();
        response.setId(song.getId());
        response.setTitle(song.getTitle());
        response.setDescription(song.getDescription());
        response.setDescription(song.getLyrics());
        response.setCoverImage(song.getCoverImage());
        response.setDuration(song.getDuration());
        response.setPlayCount(song.getPlayCount());
        response.setArtistName(song.getArtist().getFullName());
        response.setAlbumTitle(song.getAlbum() != null ? song.getAlbum().getTitle() : null);
        response.setGenreName(song.getGenre() != null ? song.getGenre().getName() : null);
        response.setCreatedAt(song.getCreatedAt());

        if (currentUser != null) {
            response.setLiked(likeRepository.existsByUser_IdAndSong_Id(currentUser.getId(), song.getId()));
        }

        return response;
    }

}
