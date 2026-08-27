package com.musicapi.controller;

import com.musicapi.dto.ApiResponse;
import com.musicapi.dto.SongViolationReportCreateDto;
import com.musicapi.security.UserPrincipal;
import com.musicapi.service.SongViolationReportService;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/song-violation-reports")
@CrossOrigin(origins = "*")@Tag(name = "Song reports", description = "Reporting songs that break the rules")
public class SongViolationReportController {

    private final SongViolationReportService reportService;

    public SongViolationReportController(SongViolationReportService reportService) {
        this.reportService = reportService;
    }

    @PostMapping("/songs/{songId}")
    public ResponseEntity<?> createReport(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @PathVariable Long songId,
            @Valid @RequestBody SongViolationReportCreateDto request
    ) {
        return ResponseEntity.ok(ApiResponse.success("Song violation report submitted successfully",
                reportService.createReport(currentUser.getId(), songId, request)));
    }

    @GetMapping("/my")
    public ResponseEntity<?> getMyReports(@AuthenticationPrincipal UserPrincipal currentUser) {
        return ResponseEntity.ok(ApiResponse.success("Your reports retrieved successfully",
                reportService.getMyReports(currentUser.getId())));
    }
}

