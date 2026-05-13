package com.musicapi.repository;

import com.musicapi.model.PopupAd;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface PopupAdRepository extends JpaRepository<PopupAd, Long> {
    @Query("""
            SELECT p FROM PopupAd p
            WHERE p.active = true
              AND (p.startAt IS NULL OR p.startAt <= :now)
              AND (p.endAt IS NULL OR p.endAt >= :now)
            ORDER BY p.createdAt DESC
            """)
    Page<PopupAd> findActivePopup(@Param("now") LocalDateTime now, Pageable pageable);
}
