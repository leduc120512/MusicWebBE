package com.musicapi.service;

import com.musicapi.error.StorageException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 * Single owner of everything that touches the upload folder.
 *
 * Before this class existed, four controllers each carried their own copy of
 * sanitizeFileName() and resolveUniqueFileName() plus inline Files.copy() calls.
 * Files land under {@code app.upload.dir} and are served back from
 * {@code /upload/**} by StaticResourceConfig.
 */
@Service
public class FileStorageService {

    /** Public URL prefix that StaticResourceConfig maps onto the upload folder. */
    private static final String PUBLIC_PREFIX = "/upload/";

    private final Path root;

    public FileStorageService(@Value("${app.upload.dir}") String uploadDir) {
        this.root = Paths.get(uploadDir).toAbsolutePath().normalize();
    }

    /**
     * Stores a file at the root of the upload folder.
     *
     * @return the public path to persist on the entity, or {@code null} when no
     *         file was supplied
     */
    public String store(MultipartFile file) {
        return store(file, null);
    }

    /**
     * Stores a file inside {@code subFolder} of the upload folder, creating the
     * folder if needed. The original name is sanitised and suffixed when it would
     * collide with an existing file.
     *
     * @return the public path to persist on the entity, or {@code null} when no
     *         file was supplied
     */
    public String store(MultipartFile file, String subFolder) {
        if (file == null || file.isEmpty()) {
            return null;
        }

        boolean nested = subFolder != null && !subFolder.isBlank();
        Path directory = nested ? root.resolve(subFolder) : root;

        try {
            Files.createDirectories(directory);
            String fileName = resolveUniqueName(directory, sanitize(file.getOriginalFilename()));
            Files.copy(file.getInputStream(), directory.resolve(fileName), StandardCopyOption.REPLACE_EXISTING);
            return nested ? PUBLIC_PREFIX + subFolder + "/" + fileName : PUBLIC_PREFIX + fileName;
        } catch (IOException e) {
            throw new StorageException("Could not store upload " + file.getOriginalFilename(), e);
        }
    }

    /** Strips anything that would be awkward in a URL and lower-cases the result. */
    private String sanitize(String original) {
        if (original == null || original.isBlank()) {
            return "file";
        }

        int dot = original.lastIndexOf('.');
        String name = dot > 0 ? original.substring(0, dot) : original;
        String extension = dot > 0 ? original.substring(dot) : "";

        name = name.replaceAll("[^a-zA-Z0-9-_.]", "-")
                .replaceAll("-{2,}", "-")
                .toLowerCase();
        extension = extension.replaceAll("[^a-zA-Z0-9.]", "").toLowerCase();

        return name.isBlank() ? "file" + extension : name + extension;
    }

    /** Appends -1, -2, ... until the name is free inside {@code directory}. */
    private String resolveUniqueName(Path directory, String sanitized) {
        int dot = sanitized.lastIndexOf('.');
        String base = dot > 0 ? sanitized.substring(0, dot) : sanitized;
        String extension = dot > 0 ? sanitized.substring(dot) : "";

        Path candidate = directory.resolve(sanitized);
        int index = 1;
        while (Files.exists(candidate)) {
            candidate = directory.resolve(base + "-" + index + extension);
            index++;
        }
        return candidate.getFileName().toString();
    }
}
