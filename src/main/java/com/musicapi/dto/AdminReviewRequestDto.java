package com.musicapi.dto;

import jakarta.validation.constraints.NotBlank;

public class AdminReviewRequestDto {
    @NotBlank
    private String status;

    private String adminNote;

    private Boolean hideComment;

    private Boolean hideSong;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAdminNote() {
        return adminNote;
    }

    public void setAdminNote(String adminNote) {
        this.adminNote = adminNote;
    }

    public Boolean getHideComment() {
        return hideComment;
    }

    public void setHideComment(Boolean hideComment) {
        this.hideComment = hideComment;
    }

    public Boolean getHideSong() {
        return hideSong;
    }

    public void setHideSong(Boolean hideSong) {
        this.hideSong = hideSong;
    }
}
