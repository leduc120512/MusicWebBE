package com.musicapi.repository;

import com.musicapi.model.SongViolationReport;
import com.musicapi.model.Role;
import com.musicapi.model.ViolationReportStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SongViolationReportRepository extends JpaRepository<SongViolationReport, Long> {
    List<SongViolationReport> findByReporterIdOrderByCreatedAtDesc(Long reporterId);

    List<SongViolationReport> findByStatusOrderByCreatedAtAsc(ViolationReportStatus status);

    Long countBySong_Artist_Id(Long artistId);

    Long countByStatus(ViolationReportStatus status);

    Long countBySong_IdAndStatus(Long songId, ViolationReportStatus status);

    Long countBySong_IdAndStatusAndReporter_Role(Long songId, ViolationReportStatus status, Role role);
}

