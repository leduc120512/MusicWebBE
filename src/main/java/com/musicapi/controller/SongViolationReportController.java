package com.musicapi.controller;

import com.musicapi.dto.ApiResponse;
import com.musicapi.dto.SongViolationReportCreateDto;
import com.musicapi.security.UserPrincipal;
import com.musicapi.service.SongViolationReportService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/song-violation-reports")
@CrossOrigin(origins = "*")
public class SongViolationReportController {
    @Autowired
    private SongViolationReportService reportService;

    @PostMapping("/songs/{songId}")
    public ResponseEntity<?> createReport(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @PathVariable Long songId,
            @Valid @RequestBody SongViolationReportCreateDto request
    ) {
        try {
            return ResponseEntity.ok(ApiResponse.success("Đã gửi báo cáo vi phạm bài hát",
                    reportService.createReport(currentUser.getId(), songId, request)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Gửi báo cáo thất bại: " + e.getMessage()));
        }
    }

    @GetMapping("/my")
    public ResponseEntity<?> getMyReports(@AuthenticationPrincipal UserPrincipal currentUser) {
        try {
            return ResponseEntity.ok(ApiResponse.success("Danh sách báo cáo của bạn",
                    reportService.getMyReports(currentUser.getId())));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Không lấy được báo cáo: " + e.getMessage()));
        }
    }
}

