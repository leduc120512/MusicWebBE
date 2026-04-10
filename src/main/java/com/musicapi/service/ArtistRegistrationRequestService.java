package com.musicapi.service;

import com.musicapi.dto.AdminReviewRequestDto;
import com.musicapi.dto.ArtistRegistrationRequestAdminResponse;
import com.musicapi.dto.ArtistRegistrationRequestCreateDto;
import com.musicapi.model.*;
import com.musicapi.repository.ArtistRegistrationRequestRepository;
import com.musicapi.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ArtistRegistrationRequestService {
    @Autowired
    private ArtistRegistrationRequestRepository requestRepository;

    @Autowired
    private UserRepository userRepository;

    public ArtistRegistrationRequest createRequest(Long userId, ArtistRegistrationRequestCreateDto dto) {
        requestRepository.findTopByUserIdAndStatusOrderByCreatedAtDesc(userId, ArtistRequestStatus.PENDING)
                .ifPresent(r -> { throw new RuntimeException("Bạn đã có yêu cầu chờ duyệt"); });

        User user = getUser(userId);
        if (user.getRole() == Role.ROLE_AUTHOR || user.getRole() == Role.ROLE_ADMIN) {
            throw new RuntimeException("Tài khoản đã là tác giả hoặc admin");
        }

        ArtistRegistrationRequest request = new ArtistRegistrationRequest();
        request.setUser(user);
        request.setReason(dto.getReason());
        request.setPortfolioUrl(dto.getPortfolioUrl());
        request.setStatus(ArtistRequestStatus.PENDING);
        return requestRepository.save(request);
    }

    public List<ArtistRegistrationRequest> getMyRequests(Long userId) {
        return requestRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public ArtistRegistrationRequest cancelMyRequest(Long userId, Long requestId) {
        ArtistRegistrationRequest request = getRequest(requestId);
        if (!request.getUser().getId().equals(userId)) {
            throw new RuntimeException("Không thể hủy yêu cầu của người khác");
        }
        if (request.getStatus() != ArtistRequestStatus.PENDING) {
            throw new RuntimeException("Chỉ hủy được yêu cầu đang chờ duyệt");
        }
        request.setStatus(ArtistRequestStatus.CANCELLED);
        return requestRepository.save(request);
    }

    public List<ArtistRegistrationRequest> getByStatus(ArtistRequestStatus status) {
        return requestRepository.findByStatusOrderByCreatedAtAsc(status);
    }

    @Transactional
    public List<ArtistRegistrationRequestAdminResponse> getByStatusOrAll(String statusText) {
        if (statusText == null || statusText.isBlank() || "ALL".equalsIgnoreCase(statusText.trim())) {
            return requestRepository.findAllByOrderByCreatedAtDesc().stream()
                    .map(this::toAdminResponse)
                    .toList();
        }

        ArtistRequestStatus status = ArtistRequestStatus.valueOf(statusText.trim().toUpperCase());
        return getByStatus(status).stream()
                .map(this::toAdminResponse)
                .toList();
    }

    @Transactional
    public ArtistRegistrationRequestAdminResponse reviewRequest(Long adminId, Long requestId, AdminReviewRequestDto dto) {
        User admin = getUser(adminId);
        if (admin.getRole() != Role.ROLE_ADMIN) {
            throw new RuntimeException("Chỉ admin mới được duyệt");
        }

        ArtistRegistrationRequest request = getRequest(requestId);
        if (request.getStatus() != ArtistRequestStatus.PENDING) {
            throw new RuntimeException("Yêu cầu này đã được xử lý trước đó");
        }

        ArtistRequestStatus nextStatus = ArtistRequestStatus.valueOf(dto.getStatus().trim().toUpperCase());
        if (nextStatus != ArtistRequestStatus.APPROVED && nextStatus != ArtistRequestStatus.REJECTED) {
            throw new RuntimeException("Status hợp lệ: APPROVED hoặc REJECTED");
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

        return toAdminResponse(requestRepository.save(request));
    }

    private ArtistRegistrationRequestAdminResponse toAdminResponse(ArtistRegistrationRequest request) {
        ArtistRegistrationRequestAdminResponse response = new ArtistRegistrationRequestAdminResponse();
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
        return userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
    }

    private ArtistRegistrationRequest getRequest(Long id) {
        return requestRepository.findById(id).orElseThrow(() -> new RuntimeException("Request not found"));
    }
}

