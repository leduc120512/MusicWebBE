package com.musicapi.service;

import com.musicapi.dto.AdminReviewRequestDto;
import com.musicapi.dto.SongViolationReportCreateDto;
import com.musicapi.dto.SongViolationReportResponse;
import com.musicapi.model.*;
import com.musicapi.repository.SongRepository;
import com.musicapi.repository.SongViolationReportRepository;
import com.musicapi.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class SongViolationReportService {
    private static final long ADMIN_PRIORITY_REPORT_THRESHOLD = 15L;

    @Autowired
    private SongViolationReportRepository reportRepository;

    @Autowired
    private SongRepository songRepository;

    @Autowired
    private UserRepository userRepository;

    public SongViolationReportResponse createReport(Long reporterId, Long songId, SongViolationReportCreateDto dto) {
        User reporter = getUser(reporterId);
        Song song = songRepository.findById(songId).orElseThrow(() -> new RuntimeException("Song not found"));

        SongViolationReport report = new SongViolationReport();
        report.setReporter(reporter);
        report.setSong(song);
        report.setType(dto.getType());
        report.setDescription(dto.getDescription());
        report.setEvidenceUrl(dto.getEvidenceUrl());
        report.setStatus(ViolationReportStatus.PENDING);

        return toDto(reportRepository.save(report));
    }

    public List<SongViolationReportResponse> getMyReports(Long userId) {
        return reportRepository.findByReporterIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::toDto)
                .toList();
    }

    public List<SongViolationReportResponse> getByStatus(ViolationReportStatus status) {
        return reportRepository.findByStatusOrderByCreatedAtAsc(status)
                .stream()
                .map(this::toDto)
                .toList();
    }

    public List<SongViolationReportResponse> getByStatusAndTab(ViolationReportStatus status, String tab) {
        String normalizedTab = tab == null ? "ALL" : tab.trim().toUpperCase();
        return reportRepository.findByStatusOrderByCreatedAtAsc(status)
                .stream()
                .map(this::toDto)
                .filter(report -> switch (normalizedTab) {
                    case "PRIORITY", "VISIBLE", "FIRST" -> report.isPriorityVisible();
                    case "NORMAL", "OTHER", "SECOND" -> !report.isPriorityVisible();
                    default -> true;
                })
                .toList();
    }

    public Map<String, Long> getTabSummary(ViolationReportStatus status) {
        List<SongViolationReportResponse> reports = reportRepository.findByStatusOrderByCreatedAtAsc(status)
                .stream()
                .map(this::toDto)
                .toList();

        long priorityCount = reports.stream().filter(SongViolationReportResponse::isPriorityVisible).count();
        long normalCount = reports.size() - priorityCount;
        return Map.of(
                "totalCount", (long) reports.size(),
                "priorityCount", priorityCount,
                "normalCount", normalCount,
                "priorityThreshold", ADMIN_PRIORITY_REPORT_THRESHOLD
        );
    }

    public SongViolationReportResponse reviewReport(Long adminId, Long reportId, AdminReviewRequestDto dto) {
        User admin = getUser(adminId);
        if (admin.getRole() != Role.ROLE_ADMIN) {
            throw new RuntimeException("Chỉ admin mới được xử lý");
        }

        SongViolationReport report = reportRepository.findById(reportId)
                .orElseThrow(() -> new RuntimeException("Report not found"));

        ViolationReportStatus nextStatus = ViolationReportStatus.valueOf(dto.getStatus().trim().toUpperCase());
        if (nextStatus != ViolationReportStatus.RESOLVED && nextStatus != ViolationReportStatus.REJECTED) {
            throw new RuntimeException("Status hợp lệ: RESOLVED hoặc REJECTED");
        }

        report.setStatus(nextStatus);
        report.setAdminNote(dto.getAdminNote());
        report.setReviewedBy(admin);
        report.setReviewedAt(LocalDateTime.now());

        if (nextStatus == ViolationReportStatus.RESOLVED && Boolean.TRUE.equals(dto.getHideSong())) {
            Song song = report.getSong();
            song.setActive(false);
            songRepository.save(song);
        }

        return toDto(reportRepository.save(report));
    }

    public SongViolationReportResponse resolveAndHideSong(Long adminId, Long reportId, String adminNote) {
        AdminReviewRequestDto request = new AdminReviewRequestDto();
        request.setStatus(ViolationReportStatus.RESOLVED.name());
        request.setHideSong(true);
        request.setAdminNote(adminNote);
        return reviewReport(adminId, reportId, request);
    }

    private SongViolationReportResponse toDto(SongViolationReport report) {
        SongViolationReportResponse response = new SongViolationReportResponse();
        response.setId(report.getId());
        response.setSongId(report.getSong() != null ? report.getSong().getId() : null);
        response.setSongTitle(report.getSong() != null ? report.getSong().getTitle() : null);
        response.setReporterId(report.getReporter() != null ? report.getReporter().getId() : null);
        response.setReporterUsername(report.getReporter() != null ? report.getReporter().getUsername() : null);
        response.setReporterRole(report.getReporter() != null && report.getReporter().getRole() != null
                ? report.getReporter().getRole().name()
                : null);
        response.setType(report.getType());
        response.setDescription(report.getDescription());
        response.setEvidenceUrl(report.getEvidenceUrl());
        response.setStatus(report.getStatus());
        Long songReportCount = report.getSong() == null || report.getSong().getId() == null
                ? 0L
                : reportRepository.countBySong_IdAndStatus(report.getSong().getId(), report.getStatus());
        Long authorReportCount = report.getSong() == null || report.getSong().getId() == null
                ? 0L
                : reportRepository.countBySong_IdAndStatusAndReporter_Role(
                        report.getSong().getId(),
                        report.getStatus(),
                        Role.ROLE_AUTHOR
                );
        response.setSongReportCount(songReportCount);
        response.setAuthorReportCount(authorReportCount);
        response.setPriorityVisible(songReportCount > ADMIN_PRIORITY_REPORT_THRESHOLD && authorReportCount > 0);
        response.setAdminNote(report.getAdminNote());
        response.setReviewedById(report.getReviewedBy() != null ? report.getReviewedBy().getId() : null);
        response.setReviewedByUsername(report.getReviewedBy() != null ? report.getReviewedBy().getUsername() : null);
        response.setReviewedAt(report.getReviewedAt());
        response.setCreatedAt(report.getCreatedAt());
        return response;
    }

    private User getUser(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
    }
}
