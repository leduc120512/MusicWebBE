package com.musicapi.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Covers the behaviour the four duplicated controller helpers used to provide.
 */
class FileStorageServiceTest {

    @TempDir
    Path uploadRoot;

    private FileStorageService storage;

    @BeforeEach
    void setUp() {
        storage = new FileStorageService(uploadRoot.toString());
    }

    private MockMultipartFile file(String name) {
        return new MockMultipartFile("file", name, "image/png", "payload".getBytes(StandardCharsets.UTF_8));
    }

    @Test
    @DisplayName("returns null when nothing was uploaded")
    void nullAndEmptyUploadsAreIgnored() {
        assertThat(storage.store(null, "banner")).isNull();
        assertThat(storage.store(new MockMultipartFile("file", "x.png", "image/png", new byte[0]), "banner")).isNull();
    }

    @Test
    @DisplayName("writes into the sub-folder and returns the public path")
    void storesInsideSubFolder() throws Exception {
        String publicPath = storage.store(file("cover.png"), "banner");

        assertThat(publicPath).isEqualTo("/upload/banner/cover.png");
        assertThat(Files.readString(uploadRoot.resolve("banner").resolve("cover.png"))).isEqualTo("payload");
    }

    @Test
    @DisplayName("writes to the upload root when no sub-folder is given")
    void storesAtRoot() {
        assertThat(storage.store(file("track.mp3"))).isEqualTo("/upload/track.mp3");
    }

    @Test
    @DisplayName("sanitises spaces, accents and upper case out of the name")
    void sanitisesTheFileName() {
        String publicPath = storage.store(file("Bản Nhạc Hay.PNG"), "banner");

        assertThat(publicPath).isEqualTo("/upload/banner/b-n-nh-c-hay.png");
    }

    @Test
    @DisplayName("suffixes the name instead of overwriting an existing file")
    void doesNotOverwriteExistingFiles() {
        assertThat(storage.store(file("cover.png"), "banner")).isEqualTo("/upload/banner/cover.png");
        assertThat(storage.store(file("cover.png"), "banner")).isEqualTo("/upload/banner/cover-1.png");
        assertThat(storage.store(file("cover.png"), "banner")).isEqualTo("/upload/banner/cover-2.png");
    }

    @Test
    @DisplayName("falls back to a usable name when the original is unusable")
    void handlesNamesWithNothingLeftAfterSanitising() {
        // every odd character becomes '-', then runs of '-' collapse to one
        assertThat(storage.store(file("???.png"), null)).isEqualTo("/upload/-.png");
        assertThat(storage.store(file(""), null)).isEqualTo("/upload/file");
    }
}
