package com.musicapi.dto;

import com.musicapi.model.CommentReportReason;
import com.musicapi.model.CommentReportStatus;

import java.time.LocalDateTime;

public class CommentReportResponse {
    private Long id;
    private Long commentId;
    private String commentContent;
    private boolean commentDeleted;
    private Long songId;
    private String songTitle;
    private Long reporterId;
    private String reporterUsername;
    private CommentReportReason reason;
    private String detail;
    private CommentReportStatus status;
    private String adminNote;
    private Long reviewedById;
    private String reviewedByUsername;
    private LocalDateTime reviewedAt;
    private LocalDateTime createdAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getCommentId() { return commentId; }
    public void setCommentId(Long commentId) { this.commentId = commentId; }
    public String getCommentContent() { return commentContent; }
    public void setCommentContent(String commentContent) { this.commentContent = commentContent; }
    public boolean isCommentDeleted() { return commentDeleted; }
    public void setCommentDeleted(boolean commentDeleted) { this.commentDeleted = commentDeleted; }
    public Long getSongId() { return songId; }
    public void setSongId(Long songId) { this.songId = songId; }
    public String getSongTitle() { return songTitle; }
    public void setSongTitle(String songTitle) { this.songTitle = songTitle; }
    public Long getReporterId() { return reporterId; }
    public void setReporterId(Long reporterId) { this.reporterId = reporterId; }
    public String getReporterUsername() { return reporterUsername; }
    public void setReporterUsername(String reporterUsername) { this.reporterUsername = reporterUsername; }
    public CommentReportReason getReason() { return reason; }
    public void setReason(CommentReportReason reason) { this.reason = reason; }
    public String getDetail() { return detail; }
    public void setDetail(String detail) { this.detail = detail; }
    public CommentReportStatus getStatus() { return status; }
    public void setStatus(CommentReportStatus status) { this.status = status; }
    public String getAdminNote() { return adminNote; }
    public void setAdminNote(String adminNote) { this.adminNote = adminNote; }
    public Long getReviewedById() { return reviewedById; }
    public void setReviewedById(Long reviewedById) { this.reviewedById = reviewedById; }
    public String getReviewedByUsername() { return reviewedByUsername; }
    public void setReviewedByUsername(String reviewedByUsername) { this.reviewedByUsername = reviewedByUsername; }
    public LocalDateTime getReviewedAt() { return reviewedAt; }
    public void setReviewedAt(LocalDateTime reviewedAt) { this.reviewedAt = reviewedAt; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}

