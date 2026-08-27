package com.musicapi.controller;

import com.musicapi.dto.ApiResponse;
import com.musicapi.dto.ArtistRegistrationRequestCreateDto;
import com.musicapi.security.UserPrincipal;
import com.musicapi.service.ArtistRegistrationRequestService;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/artist-requests")
@CrossOrigin(origins = "*")@Tag(name = "Artist requests", description = "Asking to be upgraded to ROLE_AUTHOR")
public class ArtistRegistrationRequestController {

    private final ArtistRegistrationRequestService requestService;

    public ArtistRegistrationRequestController(ArtistRegistrationRequestService requestService) {
        this.requestService = requestService;
    }

    @PostMapping("/me")
    public ResponseEntity<?> createMyRequest(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @Valid @RequestBody ArtistRegistrationRequestCreateDto request
    ) {
        return ResponseEntity.ok(ApiResponse.success("Artist request submitted successfully",
                requestService.createRequest(currentUser.getId(), request)));
    }

    @GetMapping("/me")
    public ResponseEntity<?> getMyRequests(@AuthenticationPrincipal UserPrincipal currentUser) {
        return ResponseEntity.ok(ApiResponse.success("Request history retrieved successfully", requestService.getMyRequests(currentUser.getId())));
    }

    @PutMapping("/me/{requestId}/cancel")
    public ResponseEntity<?> cancelMyRequest(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @PathVariable Long requestId
    ) {
        return ResponseEntity.ok(ApiResponse.success("Request cancelled successfully",
                requestService.cancelMyRequest(currentUser.getId(), requestId)));
    }
}

