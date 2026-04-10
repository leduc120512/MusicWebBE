package com.musicapi.repository;

import com.musicapi.model.CommentReport;
import com.musicapi.model.CommentReportStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentReportRepository extends JpaRepository<CommentReport, Long> {
    boolean existsByCommentIdAndReporterIdAndStatus(Long commentId, Long reporterId, CommentReportStatus status);

    List<CommentReport> findByStatusOrderByCreatedAtAsc(CommentReportStatus status);

    Long countByComment_Song_Artist_Id(Long artistId);

    Long countByStatus(CommentReportStatus status);
}

