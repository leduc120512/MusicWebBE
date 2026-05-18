package com.musicapi.dto;

import com.musicapi.model.ViolationReportStatus;
import com.musicapi.model.ViolationType;

import java.time.LocalDateTime;

public class SongViolationReportResponse {
    private Long id;
    private Long songId;
    private String songTitle;
    private Long reporterId;
    private String reporterUsername;
    private String reporterRole;
    private ViolationType type;
    private String description;
    private String evidenceUrl;
    private ViolationReportStatus status;
    private Long songReportCount;
    private Long authorReportCount;
    private boolean priorityVisible;
    private String adminNote;
    private Long reviewedById;
    private String reviewedByUsername;
    private LocalDateTime reviewedAt;
    private LocalDateTime createdAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getSongId() { return songId; }
    public void setSongId(Long songId) { this.songId = songId; }
    public String getSongTitle() { return songTitle; }
    public void setSongTitle(String songTitle) { this.songTitle = songTitle; }
    public Long getReporterId() { return reporterId; }
    public void setReporterId(Long reporterId) { this.reporterId = reporterId; }
    public String getReporterUsername() { return reporterUsername; }
    public void setReporterUsername(String reporterUsername) { this.reporterUsername = reporterUsername; }
    public String getReporterRole() { return reporterRole; }
    public void setReporterRole(String reporterRole) { this.reporterRole = reporterRole; }
    public ViolationType getType() { return type; }
    public void setType(ViolationType type) { this.type = type; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getEvidenceUrl() { return evidenceUrl; }
    public void setEvidenceUrl(String evidenceUrl) { this.evidenceUrl = evidenceUrl; }
    public ViolationReportStatus getStatus() { return status; }
    public void setStatus(ViolationReportStatus status) { this.status = status; }
    public Long getSongReportCount() { return songReportCount; }
    public void setSongReportCount(Long songReportCount) { this.songReportCount = songReportCount; }
    public Long getAuthorReportCount() { return authorReportCount; }
    public void setAuthorReportCount(Long authorReportCount) { this.authorReportCount = authorReportCount; }
    public boolean isPriorityVisible() { return priorityVisible; }
    public void setPriorityVisible(boolean priorityVisible) { this.priorityVisible = priorityVisible; }
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

