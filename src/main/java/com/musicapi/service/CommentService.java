package com.musicapi.service;

import com.musicapi.dto.AdminReviewRequestDto;
import com.musicapi.dto.CommentCreateRequest;
import com.musicapi.dto.CommentReportCreateRequest;
import com.musicapi.dto.CommentReportResponse;
import com.musicapi.dto.CommentResponse;
import com.musicapi.model.*;
import com.musicapi.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CommentService {
    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private CommentReportRepository commentReportRepository;

    @Autowired
    private SongRepository songRepository;

    @Autowired
    private UserRepository userRepository;

    public CommentResponse createComment(Long userId, Long songId, CommentCreateRequest request) {
        User user = getUser(userId);
        Song song = songRepository.findById(songId).orElseThrow(() -> new RuntimeException("Song not found"));

        Comment comment = new Comment();
        comment.setUser(user);
        comment.setSong(song);
        comment.setContent(request.getContent().trim());
        return map(commentRepository.save(comment));
    }

    public CommentResponse replyComment(Long userId, Long parentId, CommentCreateRequest request) {
        User user = getUser(userId);
        Comment parent = getComment(parentId);

        Comment reply = new Comment();
        reply.setUser(user);
        reply.setSong(parent.getSong());
        reply.setParent(parent);
        reply.setContent(request.getContent().trim());
        return map(commentRepository.save(reply));
    }

    public Map<String, Object> getSongComments(Long songId, int page, int size, int replySize) {
        Page<Comment> rootPage = commentRepository.findBySongIdAndParentIsNullAndDeletedFalseOrderByCreatedAtDesc(
                songId,
                PageRequest.of(page, size)
        );

        List<CommentResponse> roots = rootPage.getContent().stream().map(this::map).toList();
        for (CommentResponse root : roots) {
            fillRepliesPreview(root, replySize);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("items", roots);
        result.put("page", rootPage.getNumber());
        result.put("size", rootPage.getSize());
        result.put("totalItems", rootPage.getTotalElements());
        result.put("totalPages", rootPage.getTotalPages());
        result.put("hasNext", rootPage.hasNext());
        return result;
    }

    public Map<String, Object> getReplies(Long commentId, int page, int size) {
        getComment(commentId); // validate parent exists

        Page<Comment> replyPage = commentRepository.findByParentIdAndDeletedFalseOrderByCreatedAtAsc(
                commentId,
                PageRequest.of(page, size)
        );

        List<CommentResponse> replies = replyPage.getContent().stream().map(this::map).toList();

        Map<String, Object> result = new HashMap<>();
        result.put("parentId", commentId);
        result.put("items", replies);
        result.put("page", replyPage.getNumber());
        result.put("size", replyPage.getSize());
        result.put("totalItems", replyPage.getTotalElements());
        result.put("totalPages", replyPage.getTotalPages());
        result.put("hasNext", replyPage.hasNext());
        return result;
    }

    public CommentResponse updateComment(Long userId, Long commentId, CommentCreateRequest request) {
        Comment comment = getComment(commentId);
        if (!comment.getUser().getId().equals(userId)) {
            throw new RuntimeException("Bạn không có quyền sửa bình luận này");
        }
        comment.setContent(request.getContent().trim());
        return map(commentRepository.save(comment));
    }

    public void deleteComment(Long userId, Long commentId) {
        Comment comment = getComment(commentId);
        User user = getUser(userId);

        if (!comment.getUser().getId().equals(userId) && user.getRole() != Role.ROLE_ADMIN) {
            throw new RuntimeException("Bạn không có quyền xóa bình luận này");
        }

        comment.setDeleted(true);
        comment.setContent("[Bình luận đã bị ẩn]");
        commentRepository.save(comment);
    }

    public CommentReportResponse reportComment(Long userId, Long commentId, CommentReportCreateRequest request) {
        if (commentReportRepository.existsByCommentIdAndReporterIdAndStatus(commentId, userId, CommentReportStatus.PENDING)) {
            throw new RuntimeException("Bạn đã report bình luận này và đang chờ xử lý");
        }

        Comment comment = getComment(commentId);
        User reporter = getUser(userId);

        CommentReport report = new CommentReport();
        report.setComment(comment);
        report.setReporter(reporter);
        report.setReason(request.getReason());
        report.setDetail(request.getDetail().trim());
        report.setStatus(CommentReportStatus.PENDING);
        return mapReport(commentReportRepository.save(report));
    }

    public List<CommentReportResponse> getCommentReports(CommentReportStatus status) {
        return commentReportRepository.findByStatusOrderByCreatedAtAsc(status)
                .stream()
                .map(this::mapReport)
                .toList();
    }

    public CommentReportResponse reviewCommentReport(Long adminId, Long reportId, AdminReviewRequestDto dto) {
        User admin = getUser(adminId);
        if (admin.getRole() != Role.ROLE_ADMIN) {
            throw new RuntimeException("Chỉ admin mới được xử lý");
        }

        CommentReport report = commentReportRepository.findById(reportId)
                .orElseThrow(() -> new RuntimeException("Comment report not found"));

        CommentReportStatus status = CommentReportStatus.valueOf(dto.getStatus().trim().toUpperCase());
        if (status != CommentReportStatus.RESOLVED && status != CommentReportStatus.REJECTED) {
            throw new RuntimeException("Status hợp lệ: RESOLVED hoặc REJECTED");
        }

        report.setStatus(status);
        report.setAdminNote(dto.getAdminNote());
        report.setReviewedBy(admin);
        report.setReviewedAt(LocalDateTime.now());

        if (Boolean.TRUE.equals(dto.getHideComment())) {
            Comment comment = report.getComment();
            comment.setDeleted(true);
            comment.setContent("[Bình luận đã bị ẩn bởi quản trị viên]");
            commentRepository.save(comment);
        }

        return mapReport(commentReportRepository.save(report));
    }

    private CommentReportResponse mapReport(CommentReport report) {
        CommentReportResponse response = new CommentReportResponse();
        response.setId(report.getId());
        response.setCommentId(report.getComment() != null ? report.getComment().getId() : null);
        response.setCommentContent(report.getComment() != null ? report.getComment().getContent() : null);
        response.setCommentDeleted(report.getComment() != null && report.getComment().isDeleted());
        response.setSongId(report.getComment() != null && report.getComment().getSong() != null
                ? report.getComment().getSong().getId()
                : null);
        response.setSongTitle(report.getComment() != null && report.getComment().getSong() != null
                ? report.getComment().getSong().getTitle()
                : null);
        response.setReporterId(report.getReporter() != null ? report.getReporter().getId() : null);
        response.setReporterUsername(report.getReporter() != null ? report.getReporter().getUsername() : null);
        response.setReason(report.getReason());
        response.setDetail(report.getDetail());
        response.setStatus(report.getStatus());
        response.setAdminNote(report.getAdminNote());
        response.setReviewedById(report.getReviewedBy() != null ? report.getReviewedBy().getId() : null);
        response.setReviewedByUsername(report.getReviewedBy() != null ? report.getReviewedBy().getUsername() : null);
        response.setReviewedAt(report.getReviewedAt());
        response.setCreatedAt(report.getCreatedAt());
        return response;
    }

    private void fillRepliesPreview(CommentResponse root, int replySize) {
        int safeReplySize = Math.max(replySize, 0);
        Long totalReplies = commentRepository.countByParentIdAndDeletedFalse(root.getId());
        root.setTotalReplies(totalReplies);

        if (safeReplySize == 0 || totalReplies == 0) {
            root.setReplies(List.of());
            root.setHasMoreReplies(totalReplies > 0);
            return;
        }

        Page<Comment> previewPage = commentRepository.findByParentIdAndDeletedFalseOrderByCreatedAtAsc(
                root.getId(),
                PageRequest.of(0, safeReplySize)
        );

        root.setReplies(previewPage.getContent().stream().map(this::map).toList());
        root.setHasMoreReplies(previewPage.hasNext());
    }

    private CommentResponse map(Comment comment) {
        CommentResponse response = new CommentResponse();
        response.setId(comment.getId());
        response.setSongId(comment.getSong().getId());
        response.setParentId(comment.getParent() != null ? comment.getParent().getId() : null);
        response.setUserId(comment.getUser().getId());
        response.setUsername(comment.getUser().getUsername());
        response.setAvatar(comment.getUser().getAvatar());
        response.setContent(comment.getContent());
        response.setDeleted(comment.isDeleted());
        response.setCreatedAt(comment.getCreatedAt());
        return response;
    }

    private User getUser(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
    }

    private Comment getComment(Long id) {
        return commentRepository.findById(id).orElseThrow(() -> new RuntimeException("Comment not found"));
    }
}
