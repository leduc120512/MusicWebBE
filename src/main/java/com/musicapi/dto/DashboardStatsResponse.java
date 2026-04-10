package com.musicapi.dto;

public class DashboardStatsResponse {
    private Long totalSongs;
    private Long totalAlbums;
    private Long totalComments;
    private Long totalSongReports;
    private Long totalCommentReports;
    private Long totalLikes;
    private Long totalPlayCount;

    private Long totalUsers;
    private Long totalAuthors;
    private Long totalArtistRequests;
    private Long pendingArtistRequests;

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

    public Long getTotalComments() {
        return totalComments;
    }

    public void setTotalComments(Long totalComments) {
        this.totalComments = totalComments;
    }

    public Long getTotalSongReports() {
        return totalSongReports;
    }

    public void setTotalSongReports(Long totalSongReports) {
        this.totalSongReports = totalSongReports;
    }

    public Long getTotalCommentReports() {
        return totalCommentReports;
    }

    public void setTotalCommentReports(Long totalCommentReports) {
        this.totalCommentReports = totalCommentReports;
    }

    public Long getTotalLikes() {
        return totalLikes;
    }

    public void setTotalLikes(Long totalLikes) {
        this.totalLikes = totalLikes;
    }

    public Long getTotalPlayCount() {
        return totalPlayCount;
    }

    public void setTotalPlayCount(Long totalPlayCount) {
        this.totalPlayCount = totalPlayCount;
    }

    public Long getTotalUsers() {
        return totalUsers;
    }

    public void setTotalUsers(Long totalUsers) {
        this.totalUsers = totalUsers;
    }

    public Long getTotalAuthors() {
        return totalAuthors;
    }

    public void setTotalAuthors(Long totalAuthors) {
        this.totalAuthors = totalAuthors;
    }

    public Long getTotalArtistRequests() {
        return totalArtistRequests;
    }

    public void setTotalArtistRequests(Long totalArtistRequests) {
        this.totalArtistRequests = totalArtistRequests;
    }

    public Long getPendingArtistRequests() {
        return pendingArtistRequests;
    }

    public void setPendingArtistRequests(Long pendingArtistRequests) {
        this.pendingArtistRequests = pendingArtistRequests;
    }
}

