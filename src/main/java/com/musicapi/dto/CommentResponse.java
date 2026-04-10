package com.musicapi.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CommentResponse {
    private Long id;
    private Long songId;
    private Long parentId;
    private Long userId;
    private String username;
    private String avatar;
    private String content;
    private boolean deleted;
    private LocalDateTime createdAt;
    private Long totalReplies = 0L;
    private boolean hasMoreReplies;
    private List<CommentResponse> replies = new ArrayList<>();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getSongId() { return songId; }
    public void setSongId(Long songId) { this.songId = songId; }
    public Long getParentId() { return parentId; }
    public void setParentId(Long parentId) { this.parentId = parentId; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getAvatar() { return avatar; }
    public void setAvatar(String avatar) { this.avatar = avatar; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public boolean isDeleted() { return deleted; }
    public void setDeleted(boolean deleted) { this.deleted = deleted; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public Long getTotalReplies() { return totalReplies; }
    public void setTotalReplies(Long totalReplies) { this.totalReplies = totalReplies; }
    public boolean isHasMoreReplies() { return hasMoreReplies; }
    public void setHasMoreReplies(boolean hasMoreReplies) { this.hasMoreReplies = hasMoreReplies; }
    public List<CommentResponse> getReplies() { return replies; }
    public void setReplies(List<CommentResponse> replies) { this.replies = replies; }
}
