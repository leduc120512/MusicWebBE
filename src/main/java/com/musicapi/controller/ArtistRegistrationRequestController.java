package com.musicapi.controller;

import com.musicapi.dto.ApiResponse;
import com.musicapi.dto.ArtistRegistrationRequestCreateDto;
import com.musicapi.security.UserPrincipal;
import com.musicapi.service.ArtistRegistrationRequestService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/artist-requests")
@CrossOrigin(origins = "*")
public class ArtistRegistrationRequestController {
    @Autowired
    private ArtistRegistrationRequestService requestService;

    @PostMapping("/me")
    public ResponseEntity<?> createMyRequest(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @Valid @RequestBody ArtistRegistrationRequestCreateDto request
    ) {
        try {
            return ResponseEntity.ok(ApiResponse.success("Gửi yêu cầu làm tác giả thành công",
                    requestService.createRequest(currentUser.getId(), request)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Tạo yêu cầu thất bại: " + e.getMessage()));
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> getMyRequests(@AuthenticationPrincipal UserPrincipal currentUser) {
        try {
            return ResponseEntity.ok(ApiResponse.success("Lịch sử yêu cầu", requestService.getMyRequests(currentUser.getId())));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Không lấy được lịch sử: " + e.getMessage()));
        }
    }

    @PutMapping("/me/{requestId}/cancel")
    public ResponseEntity<?> cancelMyRequest(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @PathVariable Long requestId
    ) {
        try {
            return ResponseEntity.ok(ApiResponse.success("Đã hủy yêu cầu",
                    requestService.cancelMyRequest(currentUser.getId(), requestId)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Hủy yêu cầu thất bại: " + e.getMessage()));
        }
    }
}

