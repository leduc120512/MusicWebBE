package com.musicapi.dto;

import com.musicapi.model.Role;

public class UserRecommendationResponse {
    private Long id;
    private String username;
    private String fullName;
    private String avatar;
    private Role role;
    private Long followerCount;
    private Long totalSongs;
    private Long totalPlays;
    private Long matchedHistoryCount;
    private double score;
    private String reason;
    private boolean following;
    private boolean ollamaUsed;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getAvatar() { return avatar; }
    public void setAvatar(String avatar) { this.avatar = avatar; }
    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
    public Long getFollowerCount() { return followerCount; }
    public void setFollowerCount(Long followerCount) { this.followerCount = followerCount; }
    public Long getTotalSongs() { return totalSongs; }
    public void setTotalSongs(Long totalSongs) { this.totalSongs = totalSongs; }
    public Long getTotalPlays() { return totalPlays; }
    public void setTotalPlays(Long totalPlays) { this.totalPlays = totalPlays; }
    public Long getMatchedHistoryCount() { return matchedHistoryCount; }
    public void setMatchedHistoryCount(Long matchedHistoryCount) { this.matchedHistoryCount = matchedHistoryCount; }
    public double getScore() { return score; }
    public void setScore(double score) { this.score = score; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public boolean isFollowing() { return following; }
    public void setFollowing(boolean following) { this.following = following; }
    public boolean isOllamaUsed() { return ollamaUsed; }
    public void setOllamaUsed(boolean ollamaUsed) { this.ollamaUsed = ollamaUsed; }
}
