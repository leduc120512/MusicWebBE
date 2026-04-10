package com.musicapi.dto;

import jakarta.validation.constraints.Size;

public class ArtistProfileUpdateRequest {

    @Size(max = 120)
    private String stageName;

    @Size(max = 2500)
    private String bio;

    @Size(max = 255)
    private String coverImage;

    @Size(max = 1000)
    private String socialLinks;

    public String getStageName() {
        return stageName;
    }

    public void setStageName(String stageName) {
        this.stageName = stageName;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getCoverImage() {
        return coverImage;
    }

    public void setCoverImage(String coverImage) {
        this.coverImage = coverImage;
    }

    public String getSocialLinks() {
        return socialLinks;
    }

    public void setSocialLinks(String socialLinks) {
        this.socialLinks = socialLinks;
    }
}

