package com.musicapi.controller;

import com.musicapi.dto.AdminReviewRequestDto;
import com.musicapi.dto.ApiResponse;
import com.musicapi.model.CommentReportStatus;
import com.musicapi.model.ViolationReportStatus;
import com.musicapi.security.UserPrincipal;
import com.musicapi.service.AiCommentModerationService;
import com.musicapi.service.ArtistRegistrationRequestService;
import com.musicapi.service.CommentService;
import com.musicapi.service.DashboardService;
import com.musicapi.service.SongViolationReportService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/moderation")
@CrossOrigin(origins = "*")
public class AdminModerationController {
    @Autowired
    private ArtistRegistrationRequestService artistRequestService;

    @Autowired
    private SongViolationReportService songViolationReportService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private DashboardService dashboardService;

    @Autowired
    private AiCommentModerationService aiCommentModerationService;

    @GetMapping("/dashboard")
    public ResponseEntity<?> getAdminDashboard(
            @RequestParam(defaultValue = "false") boolean pendingOnly
    ) {
        try {
            Object data = pendingOnly
                    ? dashboardService.getGlobalPendingOnlyAdminStats()
                    : dashboardService.getGlobalAdminStats();
            return ResponseEntity.ok(ApiResponse.success("Thống kê dashboard toàn hệ thống", data));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Không lấy được dashboard: " + e.getMessage()));
        }
    }

    @GetMapping("/artist-requests")
    public ResponseEntity<?> getArtistRequests(@RequestParam(defaultValue = "ALL") String status) {
        try {
            return ResponseEntity.ok(ApiResponse.success("Danh sách yêu cầu tác giả",
                    artistRequestService.getByStatusOrAll(status)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Không lấy được danh sách: " + e.getMessage()));
        }
    }

    @PutMapping("/artist-requests/{requestId}")
    public ResponseEntity<?> reviewArtistRequest(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @PathVariable Long requestId,
            @Valid @RequestBody AdminReviewRequestDto request
    ) {
        try {
            return ResponseEntity.ok(ApiResponse.success("Đã xử lý yêu cầu tác giả",
                    artistRequestService.reviewRequest(currentUser.getId(), requestId, request)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Xử lý thất bại: " + e.getMessage()));
        }
    }

    @GetMapping("/song-violation-reports")
    public ResponseEntity<?> getSongViolationReports(
            @RequestParam(defaultValue = "PENDING") String status,
            @RequestParam(defaultValue = "ALL") String tab
    ) {
        try {
            ViolationReportStatus parsed = ViolationReportStatus.valueOf(status.trim().toUpperCase());
            return ResponseEntity.ok(ApiResponse.success("Danh sách báo cáo vi phạm bài hát",
                    songViolationReportService.getByStatusAndTab(parsed, tab)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Không lấy được danh sách: " + e.getMessage()));
        }
    }

    @GetMapping("/song-violation-reports/summary")
    public ResponseEntity<?> getSongViolationReportSummary(@RequestParam(defaultValue = "PENDING") String status) {
        try {
            ViolationReportStatus parsed = ViolationReportStatus.valueOf(status.trim().toUpperCase());
            return ResponseEntity.ok(ApiResponse.success("Thống kê tab báo cáo vi phạm bài hát",
                    songViolationReportService.getTabSummary(parsed)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Không lấy được thống kê: " + e.getMessage()));
        }
    }

    @PutMapping("/song-violation-reports/{reportId}")
    public ResponseEntity<?> reviewSongViolationReport(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @PathVariable Long reportId,
            @Valid @RequestBody AdminReviewRequestDto request
    ) {
        try {
            return ResponseEntity.ok(ApiResponse.success("Đã xử lý báo cáo bài hát",
                    songViolationReportService.reviewReport(currentUser.getId(), reportId, request)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Xử lý thất bại: " + e.getMessage()));
        }
    }

    @PutMapping("/song-violation-reports/{reportId}/resolve-hide-song")
    public ResponseEntity<?> resolveAndHideSong(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @PathVariable Long reportId,
            @RequestParam(value = "adminNote", required = false) String adminNote
    ) {
        try {
            return ResponseEntity.ok(ApiResponse.success("Đã duyệt report và ẩn bài hát",
                    songViolationReportService.resolveAndHideSong(currentUser.getId(), reportId, adminNote)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Xử lý thất bại: " + e.getMessage()));
        }
    }

    @GetMapping("/comment-reports")
    public ResponseEntity<?> getCommentReports(@RequestParam(defaultValue = "PENDING") String status) {
        try {
            CommentReportStatus parsed = CommentReportStatus.valueOf(status.trim().toUpperCase());
            return ResponseEntity.ok(ApiResponse.success("Danh sách báo cáo bình luận",
                    commentService.getCommentReports(parsed)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Không lấy được danh sách: " + e.getMessage()));
        }
    }

    @PutMapping("/comment-reports/{reportId}")
    public ResponseEntity<?> reviewCommentReport(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @PathVariable Long reportId,
            @Valid @RequestBody AdminReviewRequestDto request
    ) {
        try {
            return ResponseEntity.ok(ApiResponse.success("Đã xử lý báo cáo bình luận",
                    commentService.reviewCommentReport(currentUser.getId(), reportId, request)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Xử lý thất bại: " + e.getMessage()));
        }
    }

    @PostMapping("/comments/ai-scan-delete")
    public ResponseEntity<?> scanAndDeleteViolatingComments(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @RequestParam(defaultValue = "50") int limit,
            @RequestParam(required = false) String model
    ) {
        try {
            return ResponseEntity.ok(ApiResponse.success("Da quet va xoa binh luan vi pham",
                    aiCommentModerationService.scanAndDelete(currentUser.getId(), limit, model)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Quet binh luan that bai: " + e.getMessage()));
        }
    }
}
