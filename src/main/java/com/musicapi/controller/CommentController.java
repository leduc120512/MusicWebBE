package com.musicapi.controller;

import com.musicapi.dto.*;
import com.musicapi.security.UserPrincipal;
import com.musicapi.service.CommentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/comments")
@CrossOrigin(origins = "*")
public class CommentController {
    @Autowired
    private CommentService commentService;

    @GetMapping("/song/{songId}")
    public ResponseEntity<?> getSongComments(
            @PathVariable Long songId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "3") int replySize
    ) {
        try {
            return ResponseEntity.ok(ApiResponse.success("Danh sách bình luận",
                    commentService.getSongComments(songId, page, size, replySize)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Không lấy được bình luận: " + e.getMessage()));
        }
    }

    @GetMapping("/{commentId}/replies")
    public ResponseEntity<?> getReplies(
            @PathVariable Long commentId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        try {
            return ResponseEntity.ok(ApiResponse.success("Danh sách phản hồi",
                    commentService.getReplies(commentId, page, size)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Không lấy được phản hồi: " + e.getMessage()));
        }
    }

    @PostMapping("/song/{songId}")
    public ResponseEntity<?> createComment(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @PathVariable Long songId,
            @Valid @RequestBody CommentCreateRequest request
    ) {
        try {
            return ResponseEntity.ok(ApiResponse.success("Bình luận thành công",
                    commentService.createComment(currentUser.getId(), songId, request)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Bình luận thất bại: " + e.getMessage()));
        }
    }

    @PostMapping("/{commentId}/replies")
    public ResponseEntity<?> replyComment(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @PathVariable Long commentId,
            @Valid @RequestBody CommentCreateRequest request
    ) {
        try {
            return ResponseEntity.ok(ApiResponse.success("Phản hồi thành công",
                    commentService.replyComment(currentUser.getId(), commentId, request)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Phản hồi thất bại: " + e.getMessage()));
        }
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<?> updateComment(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @PathVariable Long commentId,
            @Valid @RequestBody CommentCreateRequest request
    ) {
        try {
            return ResponseEntity.ok(ApiResponse.success("Cập nhật bình luận thành công",
                    commentService.updateComment(currentUser.getId(), commentId, request)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Cập nhật thất bại: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<?> deleteComment(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @PathVariable Long commentId
    ) {
        try {
            commentService.deleteComment(currentUser.getId(), commentId);
            return ResponseEntity.ok(ApiResponse.success("Xóa bình luận thành công", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Xóa thất bại: " + e.getMessage()));
        }
    }

    @PostMapping("/{commentId}/report")
    public ResponseEntity<?> reportComment(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @PathVariable Long commentId,
            @Valid @RequestBody CommentReportCreateRequest request
    ) {
        try {
            return ResponseEntity.ok(ApiResponse.success("Đã gửi báo cáo bình luận",
                    commentService.reportComment(currentUser.getId(), commentId, request)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Báo cáo thất bại: " + e.getMessage()));
        }
    }
}
