package com.musicapi.dto;

import java.time.LocalDateTime;
import java.util.List;

public class AlbumResponse {
    private Long id;
    private String title;
    private String description;
    private String coverImage;
    private LocalDateTime releaseDate;
    private String artistName;
    private LocalDateTime createdAt;
    private List<SongResponse> songs;
    private Integer songCount;

    // Constructors
    public AlbumResponse() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCoverImage() { return coverImage; }
    public void setCoverImage(String coverImage) { this.coverImage = coverImage; }

    public LocalDateTime getReleaseDate() { return releaseDate; }
    public void setReleaseDate(LocalDateTime releaseDate) { this.releaseDate = releaseDate; }

    public String getArtistName() { return artistName; }
    public void setArtistName(String artistName) { this.artistName = artistName; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public List<SongResponse> getSongs() { return songs; }
    public void setSongs(List<SongResponse> songs) { this.songs = songs; }

    public Integer getSongCount() { return songCount; }
    public void setSongCount(Integer songCount) { this.songCount = songCount; }
}
