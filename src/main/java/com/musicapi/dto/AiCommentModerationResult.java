package com.musicapi.dto;

public class AiCommentModerationResult {
    private Long commentId;
    private boolean violates;
    private boolean deleted;
    private String reason;
    private String content;

    public Long getCommentId() { return commentId; }
    public void setCommentId(Long commentId) { this.commentId = commentId; }
    public boolean isViolates() { return violates; }
    public void setViolates(boolean violates) { this.violates = violates; }
    public boolean isDeleted() { return deleted; }
    public void setDeleted(boolean deleted) { this.deleted = deleted; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}
