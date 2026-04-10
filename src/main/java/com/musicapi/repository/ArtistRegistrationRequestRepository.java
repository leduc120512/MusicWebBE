package com.musicapi.repository;

import com.musicapi.model.ArtistRegistrationRequest;
import com.musicapi.model.ArtistRequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ArtistRegistrationRequestRepository extends JpaRepository<ArtistRegistrationRequest, Long> {
    List<ArtistRegistrationRequest> findByUserIdOrderByCreatedAtDesc(Long userId);

    Optional<ArtistRegistrationRequest> findTopByUserIdAndStatusOrderByCreatedAtDesc(Long userId, ArtistRequestStatus status);

    List<ArtistRegistrationRequest> findByStatusOrderByCreatedAtAsc(ArtistRequestStatus status);

    List<ArtistRegistrationRequest> findAllByOrderByCreatedAtDesc();

    Long countByStatus(ArtistRequestStatus status);
}

