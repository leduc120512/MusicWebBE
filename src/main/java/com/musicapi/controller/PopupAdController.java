package com.musicapi.controller;

import com.musicapi.dto.ApiResponse;
import com.musicapi.model.PopupAd;
import com.musicapi.service.PopupAdService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/api/popup-ads")
@CrossOrigin(origins = "*")
public class PopupAdController {
    private static final String UPLOAD_DIR = "D:/web nhac/duan1/upload/popup";

    @Autowired
    private PopupAdService popupAdService;

    @GetMapping("/active")
    public ApiResponse<?> getActivePopup() {
        return ApiResponse.success("Popup dang hien thi", popupAdService.getActive().orElse(null));
    }

    @GetMapping
    public ApiResponse<?> getAll() {
        return ApiResponse.success("Danh sach popup quang cao", popupAdService.getAll());
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<?> create(
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
        try {
            PopupAd popupAd = new PopupAd();
            popupAd.setTitle(title);
            popupAd.setContent(content);
            popupAd.setTargetUrl(targetUrl);
            popupAd.setActive(active);
            popupAd.setStartAt(startAt);
            popupAd.setEndAt(endAt);
            popupAd.setImage(saveImage(firstFile(imageFile, imageFileAlias, fileAlias)));
            return ApiResponse.success("Tao popup quang cao thanh cong", popupAdService.create(popupAd));
        } catch (Exception e) {
            return ApiResponse.error("Tao popup quang cao that bai: " + e.getMessage());
        }
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<?> update(
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
        try {
            PopupAd current = popupAdService.getById(id);
            PopupAd request = new PopupAd();
            request.setTitle(title);
            request.setContent(content);
            request.setTargetUrl(targetUrl);
            request.setActive(active == null ? current.isActive() : active);
            request.setStartAt(startAt == null ? current.getStartAt() : startAt);
            request.setEndAt(endAt == null ? current.getEndAt() : endAt);
            String image = saveImage(firstFile(imageFile, imageFileAlias, fileAlias));
            request.setImage(image == null ? current.getImage() : image);
            return ApiResponse.success("Cap nhat popup quang cao thanh cong", popupAdService.update(id, request));
        } catch (Exception e) {
            return ApiResponse.error("Cap nhat popup quang cao that bai: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ApiResponse<?> delete(@PathVariable Long id) {
        popupAdService.delete(id);
        return ApiResponse.success("Xoa popup quang cao thanh cong", null);
    }

    private MultipartFile firstFile(MultipartFile... files) {
        for (MultipartFile file : files) {
            if (file != null && !file.isEmpty()) {
                return file;
            }
        }
        return null;
    }

    private String saveImage(MultipartFile imageFile) throws Exception {
        if (imageFile == null || imageFile.isEmpty()) {
            return null;
        }

        Path dir = Paths.get(UPLOAD_DIR);
        Files.createDirectories(dir);
        String safeName = sanitizeFileName(imageFile.getOriginalFilename());
        String fileName = resolveUniqueFileName(dir, UUID.randomUUID() + "_" + safeName);
        Files.copy(imageFile.getInputStream(), dir.resolve(fileName), StandardCopyOption.REPLACE_EXISTING);
        return "/upload/popup/" + fileName;
    }

    private String sanitizeFileName(String original) {
        if (original == null || original.isBlank()) return "popup";
        int dot = original.lastIndexOf('.');
        String name = (dot > 0) ? original.substring(0, dot) : original;
        String ext  = (dot > 0) ? original.substring(dot) : "";

        name = name.replaceAll("[^a-zA-Z0-9-_\\.]", "-")
                .replaceAll("-{2,}", "-")
                .toLowerCase();
        ext = ext.replaceAll("[^a-zA-Z0-9\\.]", "").toLowerCase();

        if (name.isBlank()) name = "popup";
        return name + ext;
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
