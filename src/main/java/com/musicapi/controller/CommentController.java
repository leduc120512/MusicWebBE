package com.musicapi.controller;

import com.musicapi.dto.*;
import com.musicapi.security.UserPrincipal;
import com.musicapi.service.CommentService;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/comments")
@CrossOrigin(origins = "*")@Tag(name = "Comments", description = "Comments and replies on songs")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @GetMapping("/song/{songId}")
    public ResponseEntity<?> getSongComments(
            @PathVariable Long songId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "3") int replySize
    ) {
        return ResponseEntity.ok(ApiResponse.success("Danh sách bình luận",
                commentService.getSongComments(songId, page, size, replySize)));
    }

    @GetMapping("/{commentId}/replies")
    public ResponseEntity<?> getReplies(
            @PathVariable Long commentId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return ResponseEntity.ok(ApiResponse.success("Danh sách phản hồi",
                commentService.getReplies(commentId, page, size)));
    }

    @PostMapping("/song/{songId}")
    public ResponseEntity<?> createComment(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @PathVariable Long songId,
            @Valid @RequestBody CommentCreateRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success("Comment posted successfully",
                commentService.createComment(currentUser.getId(), songId, request)));
    }

    @PostMapping("/{commentId}/replies")
    public ResponseEntity<?> replyComment(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @PathVariable Long commentId,
            @Valid @RequestBody CommentCreateRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success("Reply posted successfully",
                commentService.replyComment(currentUser.getId(), commentId, request)));
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<?> updateComment(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @PathVariable Long commentId,
            @Valid @RequestBody CommentCreateRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success("Comment updated successfully",
                commentService.updateComment(currentUser.getId(), commentId, request)));
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<?> deleteComment(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @PathVariable Long commentId
    ) {
        commentService.deleteComment(currentUser.getId(), commentId);
        return ResponseEntity.ok(ApiResponse.success("Comment deleted successfully", null));
    }

    @PostMapping("/{commentId}/report")
    public ResponseEntity<?> reportComment(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @PathVariable Long commentId,
            @Valid @RequestBody CommentReportCreateRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success("Comment report submitted successfully",
                commentService.reportComment(currentUser.getId(), commentId, request)));
    }
}
