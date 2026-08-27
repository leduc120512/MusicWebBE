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
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/moderation")
@CrossOrigin(origins = "*")@Tag(name = "Admin moderation", description = "Reviewing artist requests and abuse reports (ROLE_ADMIN)")
public class AdminModerationController {

    private final ArtistRegistrationRequestService artistRequestService;
    private final SongViolationReportService songViolationReportService;
    private final CommentService commentService;
    private final DashboardService dashboardService;
    private final AiCommentModerationService aiCommentModerationService;

    public AdminModerationController(
            ArtistRegistrationRequestService artistRequestService,
            SongViolationReportService songViolationReportService,
            CommentService commentService,
            DashboardService dashboardService,
            AiCommentModerationService aiCommentModerationService
    ) {
        this.artistRequestService = artistRequestService;
        this.songViolationReportService = songViolationReportService;
        this.commentService = commentService;
        this.dashboardService = dashboardService;
        this.aiCommentModerationService = aiCommentModerationService;
    }

    @GetMapping("/dashboard")
    public ResponseEntity<?> getAdminDashboard(
            @RequestParam(defaultValue = "false") boolean pendingOnly
    ) {
        Object data = pendingOnly
                ? dashboardService.getGlobalPendingOnlyAdminStats()
                : dashboardService.getGlobalAdminStats();
        return ResponseEntity.ok(ApiResponse.success("System dashboard statistics retrieved successfully", data));
    }

    @GetMapping("/artist-requests")
    public ResponseEntity<?> getArtistRequests(@RequestParam(defaultValue = "ALL") String status) {
        return ResponseEntity.ok(ApiResponse.success("Artist requests retrieved successfully",
                artistRequestService.getByStatusOrAll(status)));
    }

    @PutMapping("/artist-requests/{requestId}")
    public ResponseEntity<?> reviewArtistRequest(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @PathVariable Long requestId,
            @Valid @RequestBody AdminReviewRequestDto request
    ) {
        return ResponseEntity.ok(ApiResponse.success("Artist request resolved successfully",
                artistRequestService.reviewRequest(currentUser.getId(), requestId, request)));
    }

    @GetMapping("/song-violation-reports")
    public ResponseEntity<?> getSongViolationReports(
            @RequestParam(defaultValue = "PENDING") String status,
            @RequestParam(defaultValue = "ALL") String tab
    ) {
        ViolationReportStatus parsed = ViolationReportStatus.valueOf(status.trim().toUpperCase());
        return ResponseEntity.ok(ApiResponse.success("Song violation reports retrieved successfully",
                songViolationReportService.getByStatusAndTab(parsed, tab)));
    }

    @GetMapping("/song-violation-reports/summary")
    public ResponseEntity<?> getSongViolationReportSummary(@RequestParam(defaultValue = "PENDING") String status) {
        ViolationReportStatus parsed = ViolationReportStatus.valueOf(status.trim().toUpperCase());
        return ResponseEntity.ok(ApiResponse.success("Song violation report statistics retrieved successfully",
                songViolationReportService.getTabSummary(parsed)));
    }

    @PutMapping("/song-violation-reports/{reportId}")
    public ResponseEntity<?> reviewSongViolationReport(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @PathVariable Long reportId,
            @Valid @RequestBody AdminReviewRequestDto request
    ) {
        return ResponseEntity.ok(ApiResponse.success("Song report resolved successfully",
                songViolationReportService.reviewReport(currentUser.getId(), reportId, request)));
    }

    @PutMapping("/song-violation-reports/{reportId}/resolve-hide-song")
    public ResponseEntity<?> resolveAndHideSong(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @PathVariable Long reportId,
            @RequestParam(value = "adminNote", required = false) String adminNote
    ) {
        return ResponseEntity.ok(ApiResponse.success("Report approved and the song was hidden",
                songViolationReportService.resolveAndHideSong(currentUser.getId(), reportId, adminNote)));
    }

    @GetMapping("/comment-reports")
    public ResponseEntity<?> getCommentReports(@RequestParam(defaultValue = "PENDING") String status) {
        CommentReportStatus parsed = CommentReportStatus.valueOf(status.trim().toUpperCase());
        return ResponseEntity.ok(ApiResponse.success("Comment reports retrieved successfully",
                commentService.getCommentReports(parsed)));
    }

    @PutMapping("/comment-reports/{reportId}")
    public ResponseEntity<?> reviewCommentReport(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @PathVariable Long reportId,
            @Valid @RequestBody AdminReviewRequestDto request
    ) {
        return ResponseEntity.ok(ApiResponse.success("Comment report resolved successfully",
                commentService.reviewCommentReport(currentUser.getId(), reportId, request)));
    }

    @PostMapping("/comments/ai-scan-delete")
    public ResponseEntity<?> scanAndDeleteViolatingComments(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @RequestParam(defaultValue = "50") int limit,
            @RequestParam(required = false) String model
    ) {
        return ResponseEntity.ok(ApiResponse.success("Violating comments scanned and removed",
                aiCommentModerationService.scanAndDelete(currentUser.getId(), limit, model)));
    }
}
