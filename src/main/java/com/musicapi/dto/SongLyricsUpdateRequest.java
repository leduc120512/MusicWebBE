package com.musicapi.dto;

import jakarta.validation.constraints.NotBlank;

public class SongLyricsUpdateRequest {
    @NotBlank
    private String lyrics;

    public String getLyrics() {
        return lyrics;
    }

    public void setLyrics(String lyrics) {
        this.lyrics = lyrics;
    }
}

