package com.musicapi.controller;

import com.musicapi.dto.ApiResponse;
import com.musicapi.model.Banner;
import com.musicapi.service.BannerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;
import java.nio.file.*;

@RestController
@RequestMapping("/api/banners")
@CrossOrigin(origins = "*")
public class BannerController {

    private final String UPLOAD_DIR = "D:/web nhac/duan1/upload/banner";

    @Autowired
    private BannerService bannerService;

    @GetMapping
    public ApiResponse getAll() {
        List<Banner> banners = bannerService.getAll();
        return ApiResponse.success("Danh sách banner", banners);
    }

    @GetMapping("/{id}")
    public ApiResponse getById(@PathVariable Long id) {
        Banner banner = bannerService.getById(id);
        return ApiResponse.success("Chi tiết banner", banner);
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse create(@RequestParam("note") String note,
                              @RequestParam("image") MultipartFile imageFile) {
        try {
            // Thư mục lưu banner
            String uploadDir = "D:/web nhac/duan1/upload/banner";
            Path dir = Paths.get(uploadDir);
            Files.createDirectories(dir);

            // Giữ tên file gốc, xử lý trùng tên
            String sanitized = sanitizeFileName(imageFile.getOriginalFilename());
            String finalName = resolveUniqueFileName(dir, sanitized);

            // Lưu file
            Path savePath = dir.resolve(finalName);
            Files.copy(imageFile.getInputStream(), savePath, StandardCopyOption.REPLACE_EXISTING);

            // Tạo Banner object
            Banner banner = new Banner();
            banner.setNote(note);
            banner.setImage("/upload/banner/" + finalName); // FE dùng đường dẫn này

            Banner saved = bannerService.create(banner);
            return ApiResponse.success("Tạo banner thành công", saved);

        } catch (Exception e) {
            return ApiResponse.error("Lỗi khi upload banner: " + e.getMessage());
        }
    }

    private String sanitizeFileName(String original) {
        if (original == null || original.isBlank()) return "file";
        int dot = original.lastIndexOf('.');
        String name = (dot > 0) ? original.substring(0, dot) : original;
        String ext  = (dot > 0) ? original.substring(dot) : "";

        // bỏ ký tự lạ, thay khoảng trắng -> '-', gộp nhiều '-'
        name = name.replaceAll("[^a-zA-Z0-9-_\\.]", "-")
                .replaceAll("-{2,}", "-")
                .toLowerCase();
        ext  = ext.replaceAll("[^a-zA-Z0-9\\.]", "").toLowerCase();

        if (name.isBlank()) name = "file";
        return name + ext;
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
    public ApiResponse update(
            @PathVariable Long id,
            @RequestParam(value = "note", required = false) String note,
            @RequestParam(value = "image", required = false) MultipartFile imageFile
    ) {
        try {
            Banner banner = bannerService.getById(id);

            // update note
            if (note != null) banner.setNote(note);

            // update image nếu có
            if (imageFile != null && !imageFile.isEmpty()) {
                String uploadDir = "D:/web nhac/duan1/upload/banner";
                Path dir = Paths.get(uploadDir);
                Files.createDirectories(dir);

                String sanitized = sanitizeFileName(imageFile.getOriginalFilename());
                String finalName = resolveUniqueFileName(dir, sanitized);

                Files.copy(
                        imageFile.getInputStream(),
                        dir.resolve(finalName),
                        StandardCopyOption.REPLACE_EXISTING
                );

                banner.setImage("/upload/banner/" + finalName);
            }

            Banner updated = bannerService.update(id, banner);
            return ApiResponse.success("Cập nhật banner thành công", updated);

        } catch (Exception e) {
            return ApiResponse.error("Lỗi khi cập nhật banner: " + e.getMessage());
        }
    }


    @DeleteMapping("/{id}")
    public ApiResponse delete(@PathVariable Long id) {
        bannerService.delete(id);
        return ApiResponse.success("Xóa banner thành công", null);
    }
}
