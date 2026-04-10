package com.musicapi.dto;

import java.time.LocalDateTime;

public class ArtistProfileResponse {
    private Long artistId;
    private String username;
    private String fullName;
    private String avatar;

    private String stageName;
    private String bio;
    private String coverImage;
    private String socialLinks;

    private Long totalSongs;
    private Long totalAlbums;
    private Long totalPlays;
    private Long followerCount;

    private LocalDateTime profileUpdatedAt;

    public Long getArtistId() {
        return artistId;
    }

    public void setArtistId(Long artistId) {
        this.artistId = artistId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getStageName() {
        return stageName;
    }

    public void setStageName(String stageName) {
        this.stageName = stageName;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getCoverImage() {
        return coverImage;
    }

    public void setCoverImage(String coverImage) {
        this.coverImage = coverImage;
    }

    public String getSocialLinks() {
        return socialLinks;
    }

    public void setSocialLinks(String socialLinks) {
        this.socialLinks = socialLinks;
    }

    public Long getTotalSongs() {
        return totalSongs;
    }

    public void setTotalSongs(Long totalSongs) {
        this.totalSongs = totalSongs;
    }

    public Long getTotalAlbums() {
        return totalAlbums;
    }

    public void setTotalAlbums(Long totalAlbums) {
        this.totalAlbums = totalAlbums;
    }

    public Long getTotalPlays() {
        return totalPlays;
    }

    public void setTotalPlays(Long totalPlays) {
        this.totalPlays = totalPlays;
    }

    public Long getFollowerCount() {
        return followerCount;
    }

    public void setFollowerCount(Long followerCount) {
        this.followerCount = followerCount;
    }

    public LocalDateTime getProfileUpdatedAt() {
        return profileUpdatedAt;
    }

    public void setProfileUpdatedAt(LocalDateTime profileUpdatedAt) {
        this.profileUpdatedAt = profileUpdatedAt;
    }
}

