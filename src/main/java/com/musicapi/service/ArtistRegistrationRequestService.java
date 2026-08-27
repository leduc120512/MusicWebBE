package com.musicapi.service;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import com.musicapi.dto.AdminReviewRequestDto;
import com.musicapi.dto.ArtistRegistrationRequestResponse;
import com.musicapi.dto.ArtistRegistrationRequestCreateDto;
import com.musicapi.model.*;
import com.musicapi.repository.ArtistRegistrationRequestRepository;
import com.musicapi.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ArtistRegistrationRequestService {

    private final ArtistRegistrationRequestRepository requestRepository;
    private final UserRepository userRepository;

    public ArtistRegistrationRequestService(ArtistRegistrationRequestRepository requestRepository, UserRepository userRepository) {
        this.requestRepository = requestRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public ArtistRegistrationRequestResponse createRequest(Long userId, ArtistRegistrationRequestCreateDto dto) {
        requestRepository.findTopByUserIdAndStatusOrderByCreatedAtDesc(userId, ArtistRequestStatus.PENDING)
                .ifPresent(r -> { throw new ResponseStatusException(HttpStatus.CONFLICT, "You already have a request awaiting review"); });

        User user = getUser(userId);
        if (user.getRole() == Role.ROLE_AUTHOR || user.getRole() == Role.ROLE_ADMIN) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "This account is already an artist or an administrator");
        }

        ArtistRegistrationRequest request = new ArtistRegistrationRequest();
        request.setUser(user);
        request.setReason(dto.getReason());
        request.setPortfolioUrl(dto.getPortfolioUrl());
        request.setStatus(ArtistRequestStatus.PENDING);
        return toResponse(requestRepository.save(request));
    }

    @Transactional(readOnly = true)
    public List<ArtistRegistrationRequestResponse> getMyRequests(Long userId) {
        return requestRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public ArtistRegistrationRequestResponse cancelMyRequest(Long userId, Long requestId) {
        ArtistRegistrationRequest request = getRequest(requestId);
        if (!request.getUser().getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You cannot cancel someone else’s request");
        }
        if (request.getStatus() != ArtistRequestStatus.PENDING) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only a pending request can be cancelled");
        }
        request.setStatus(ArtistRequestStatus.CANCELLED);
        return toResponse(requestRepository.save(request));
    }

    public List<ArtistRegistrationRequest> getByStatus(ArtistRequestStatus status) {
        return requestRepository.findByStatusOrderByCreatedAtAsc(status);
    }

    @Transactional
    public List<ArtistRegistrationRequestResponse> getByStatusOrAll(String statusText) {
        if (statusText == null || statusText.isBlank() || "ALL".equalsIgnoreCase(statusText.trim())) {
            return requestRepository.findAllByOrderByCreatedAtDesc().stream()
                    .map(this::toResponse)
                    .toList();
        }

        ArtistRequestStatus status = ArtistRequestStatus.valueOf(statusText.trim().toUpperCase());
        return getByStatus(status).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public ArtistRegistrationRequestResponse reviewRequest(Long adminId, Long requestId, AdminReviewRequestDto dto) {
        User admin = getUser(adminId);
        if (admin.getRole() != Role.ROLE_ADMIN) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only an administrator can review this request");
        }

        ArtistRegistrationRequest request = getRequest(requestId);
        if (request.getStatus() != ArtistRequestStatus.PENDING) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "This request has already been resolved");
        }

        ArtistRequestStatus nextStatus = ArtistRequestStatus.valueOf(dto.getStatus().trim().toUpperCase());
        if (nextStatus != ArtistRequestStatus.APPROVED && nextStatus != ArtistRequestStatus.REJECTED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Status must be APPROVED or REJECTED");
        }

        request.setStatus(nextStatus);
        request.setAdminNote(dto.getAdminNote());
        request.setReviewedBy(admin);
        request.setReviewedAt(LocalDateTime.now());

        if (nextStatus == ArtistRequestStatus.APPROVED) {
            User user = request.getUser();
            user.setRole(Role.ROLE_AUTHOR);
            userRepository.save(user);
        }

        return toResponse(requestRepository.save(request));
    }

    private ArtistRegistrationRequestResponse toResponse(ArtistRegistrationRequest request) {
        ArtistRegistrationRequestResponse response = new ArtistRegistrationRequestResponse();
        response.setId(request.getId());
        response.setReason(request.getReason());
        response.setPortfolioUrl(request.getPortfolioUrl());
        response.setStatus(request.getStatus());
        response.setAdminNote(request.getAdminNote());
        response.setReviewedAt(request.getReviewedAt());
        response.setCreatedAt(request.getCreatedAt());

        User requester = request.getUser();
        if (requester != null) {
            response.setUserId(requester.getId());
            response.setUsername(requester.getUsername());
            response.setEmail(requester.getEmail());
            response.setFullName(requester.getFullName());
            response.setAvatar(requester.getAvatar());
        }

        User reviewer = request.getReviewedBy();
        if (reviewer != null) {
            response.setReviewedById(reviewer.getId());
            response.setReviewedByUsername(reviewer.getUsername());
        }

        return response;
    }

    private User getUser(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }

    private ArtistRegistrationRequest getRequest(Long id) {
        return requestRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Request not found"));
    }
}

