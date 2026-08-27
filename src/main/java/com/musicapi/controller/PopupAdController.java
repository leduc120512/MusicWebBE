package com.musicapi.controller;

import com.musicapi.dto.ApiResponse;
import com.musicapi.service.FileStorageService;
import com.musicapi.model.PopupAd;
import com.musicapi.service.PopupAdService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/popup-ads")
@CrossOrigin(origins = "*")@Tag(name = "Popup ads", description = "Site-wide popup advertisements")
public class PopupAdController {

    private final PopupAdService popupAdService;

    private final FileStorageService fileStorageService;

    public PopupAdController(PopupAdService popupAdService, FileStorageService fileStorageService) {
        this.popupAdService = popupAdService;
        this.fileStorageService = fileStorageService;
    }
    private static final Logger log = LoggerFactory.getLogger(PopupAdController.class);


    @GetMapping("/active")
    public ResponseEntity<?> getActivePopup() {
        return ResponseEntity.ok(
                ApiResponse.success("Active popup ad retrieved successfully", popupAdService.getActive().orElse(null)));
    }

    @GetMapping
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(ApiResponse.success("Popup ads retrieved successfully", popupAdService.getAll()));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> create(
            @RequestParam("title") String title,
            @RequestParam(value = "content", required = false) String content,
            @RequestParam(value = "targetUrl", required = false) String targetUrl,
            @RequestParam(value = "active", defaultValue = "true") boolean active,
            @RequestParam(value = "startAt", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startAt,
            @RequestParam(value = "endAt", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endAt,
            @RequestParam(value = "image", required = false) MultipartFile imageFile,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFileAlias,
            @RequestParam(value = "file", required = false) MultipartFile fileAlias
    ) {
        PopupAd popupAd = new PopupAd();
        popupAd.setTitle(title);
        popupAd.setContent(content);
        popupAd.setTargetUrl(targetUrl);
        popupAd.setActive(active);
        popupAd.setStartAt(startAt);
        popupAd.setEndAt(endAt);
        popupAd.setImage(fileStorageService.store(firstFile(imageFile, imageFileAlias, fileAlias), "popup"));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Popup ad created successfully", popupAdService.create(popupAd)));
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> update(
            @PathVariable Long id,
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "content", required = false) String content,
            @RequestParam(value = "targetUrl", required = false) String targetUrl,
            @RequestParam(value = "active", required = false) Boolean active,
            @RequestParam(value = "startAt", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startAt,
            @RequestParam(value = "endAt", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endAt,
            @RequestParam(value = "image", required = false) MultipartFile imageFile,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFileAlias,
            @RequestParam(value = "file", required = false) MultipartFile fileAlias
    ) {
        // A missing popup surfaces as ResponseStatusException(404) from the service
        // and is deliberately not caught here, so the caller still gets a 404.
        PopupAd current = popupAdService.getById(id);

        PopupAd request = new PopupAd();
        request.setTitle(title);
        request.setContent(content);
        request.setTargetUrl(targetUrl);
        request.setActive(active == null ? current.isActive() : active);
        request.setStartAt(startAt == null ? current.getStartAt() : startAt);
        request.setEndAt(endAt == null ? current.getEndAt() : endAt);
        String image = fileStorageService.store(firstFile(imageFile, imageFileAlias, fileAlias), "popup");
        request.setImage(image == null ? current.getImage() : image);
        return ResponseEntity.ok(
                ApiResponse.success("Popup ad updated successfully", popupAdService.update(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        popupAdService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Popup ad deleted successfully", null));
    }

    private MultipartFile firstFile(MultipartFile... files) {
        for (MultipartFile file : files) {
            if (file != null && !file.isEmpty()) {
                return file;
            }
        }
        return null;
    }

private String resolveUniqueFileName(Path dir, String sanitized) {
        int dot = sanitized.lastIndexOf('.');
        String base = (dot > 0) ? sanitized.substring(0, dot) : sanitized;
        String ext  = (dot > 0) ? sanitized.substring(dot) : "";

        Path path = dir.resolve(sanitized);
        int index = 1;
        while (Files.exists(path)) {
            path = dir.resolve(base + "-" + index + ext);
            index++;
        }
        return path.getFileName().toString();
    }
}
