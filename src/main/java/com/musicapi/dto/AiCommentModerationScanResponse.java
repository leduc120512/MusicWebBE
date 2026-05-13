package com.musicapi.dto;

import java.util.List;

public class AiCommentModerationScanResponse {
    private int scanned;
    private int violations;
    private int deleted;
    private String model;
    private List<AiCommentModerationResult> items;

    public int getScanned() { return scanned; }
    public void setScanned(int scanned) { this.scanned = scanned; }
    public int getViolations() { return violations; }
    public void setViolations(int violations) { this.violations = violations; }
    public int getDeleted() { return deleted; }
    public void setDeleted(int deleted) { this.deleted = deleted; }
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    public List<AiCommentModerationResult> getItems() { return items; }
    public void setItems(List<AiCommentModerationResult> items) { this.items = items; }
}
