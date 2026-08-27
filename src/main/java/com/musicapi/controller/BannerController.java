package com.musicapi.controller;

import com.musicapi.dto.ApiResponse;
import com.musicapi.service.FileStorageService;
import com.musicapi.model.Banner;
import com.musicapi.service.BannerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.*;
import java.util.List;

@RestController
@RequestMapping("/api/banners")
@CrossOrigin(origins = "*")@Tag(name = "Banners", description = "Home page banners")
public class BannerController {

    private final BannerService bannerService;

    private final FileStorageService fileStorageService;

    public BannerController(BannerService bannerService, FileStorageService fileStorageService) {
        this.bannerService = bannerService;
        this.fileStorageService = fileStorageService;
    }

    private static final Logger log = LoggerFactory.getLogger(BannerController.class);


    @GetMapping
    public ResponseEntity<?> getAll() {
        List<Banner> banners = bannerService.getAll();
        return ResponseEntity.ok(ApiResponse.success("Banners retrieved successfully", banners));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        Banner banner = bannerService.getById(id);
        return ResponseEntity.ok(ApiResponse.success("Banner retrieved successfully", banner));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> create(@RequestParam("note") String note,
                                    @RequestParam("image") MultipartFile imageFile) {
        Banner banner = new Banner();
        banner.setNote(note);
        banner.setImage(fileStorageService.store(imageFile, "banner"));

        Banner saved = bannerService.create(banner);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Banner created successfully", saved));
    }

private String resolveUniqueFileName(Path dir, String sanitized) {
        int dot = sanitized.lastIndexOf('.');
        String base = (dot > 0) ? sanitized.substring(0, dot) : sanitized;
        String ext  = (dot > 0) ? sanitized.substring(dot) : "";

        Path p = dir.resolve(sanitized);
        int i = 1;
        while (Files.exists(p)) {
            p = dir.resolve(base + "-" + i + ext);
            i++;
        }
        return p.getFileName().toString();
    }

    @PutMapping(
            value = "/{id}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<?> update(
            @PathVariable Long id,
            @RequestParam(value = "note", required = false) String note,
            @RequestParam(value = "image", required = false) MultipartFile imageFile
    ) {
        // A missing banner surfaces as ResponseStatusException(404) from the service
        // and is deliberately not caught here, so the caller still gets a 404.
        Banner banner = bannerService.getById(id);

        if (note != null) {
            banner.setNote(note);
        }

        String imagePath = fileStorageService.store(imageFile, "banner");
        if (imagePath != null) {
            banner.setImage(imagePath);
        }

        Banner updated = bannerService.update(id, banner);
        return ResponseEntity.ok(ApiResponse.success("Banner updated successfully", updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        bannerService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Banner deleted successfully", null));
    }
}
