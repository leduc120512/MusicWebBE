package com.musicapi.dto;

import jakarta.validation.constraints.NotBlank;

public class ArtistRegistrationRequestCreateDto {
    @NotBlank
    private String reason;
    private String portfolioUrl;

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getPortfolioUrl() {
        return portfolioUrl;
    }

    public void setPortfolioUrl(String portfolioUrl) {
        this.portfolioUrl = portfolioUrl;
    }
}

