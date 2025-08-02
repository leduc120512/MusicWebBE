package com.musicapi.controller;

import com.musicapi.dto.ApiResponse;
import com.musicapi.model.Banner;
import com.musicapi.service.BannerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/banners")
@CrossOrigin(origins = "*")
public class BannerController {

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

    @PostMapping
    public ApiResponse create(@RequestBody Banner banner) {
        Banner saved = bannerService.create(banner);
        return ApiResponse.success("Tạo banner thành công", saved);
    }

    @PutMapping("/{id}")
    public ApiResponse update(@PathVariable Long id, @RequestBody Banner banner) {
        Banner updated = bannerService.update(id, banner);
        return ApiResponse.success("Cập nhật banner thành công", updated);
    }

    @DeleteMapping("/{id}")
    public ApiResponse delete(@PathVariable Long id) {
        bannerService.delete(id);
        return ApiResponse.success("Xóa banner thành công", null);
    }
}
