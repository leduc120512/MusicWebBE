package com.musicapi.repository;

import com.musicapi.model.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    Page<Comment> findByDeletedFalseOrderByCreatedAtDesc(Pageable pageable);

    Page<Comment> findBySongIdAndParentIsNullAndDeletedFalseOrderByCreatedAtDesc(Long songId, Pageable pageable);

    Page<Comment> findByParentIdAndDeletedFalseOrderByCreatedAtAsc(Long parentId, Pageable pageable);

    Long countByParentIdAndDeletedFalse(Long parentId);

    Long countBySong_Artist_IdAndDeletedFalse(Long artistId);

    Long countByDeletedFalse();
}
