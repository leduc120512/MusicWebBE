package com.musicapi.dto;

import com.musicapi.model.CommentReportReason;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CommentReportCreateRequest {
    @NotNull
    private CommentReportReason reason;

    @NotBlank
    private String detail;

    public CommentReportReason getReason() {
        return reason;
    }

    public void setReason(CommentReportReason reason) {
        this.reason = reason;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }
}

