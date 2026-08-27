package com.musicapi.controller;

import com.musicapi.dto.ApiResponse;
import com.musicapi.dto.LoginRequest;
import com.musicapi.dto.SignUpRequest;
import com.musicapi.dto.UserSummary;
import com.musicapi.model.Role;
import com.musicapi.security.UserPrincipal;
import com.musicapi.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * HTTP layer for authentication and user administration.
 *
 * The controller only binds and delegates: {@link AuthService} owns the rules
 * and signals failures with ResponseStatusException, which GlobalExceptionHandler
 * renders in the standard envelope.
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
@Tag(name = "Authentication", description = "Sign in, sign up and user administration")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/signin")
    @Operation(summary = "Sign in and receive a JWT")
    public ResponseEntity<?> signIn(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Login successful", authService.signIn(request)));
    }

    @PostMapping(value = "/signup", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Register a new account", description = "multipart/form-data; the avatar file is optional")
    public ResponseEntity<?> signUp(
            @Valid @ModelAttribute SignUpRequest request,
            @RequestParam(value = "avatar", required = false) MultipartFile avatarFile
    ) {
        UserSummary created = authService.signUp(request, avatarFile);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("User registered successfully", created));
    }

    @GetMapping("/me")
    @Operation(summary = "Profile of the signed-in caller")
    public ResponseEntity<?> getCurrentUser(@AuthenticationPrincipal UserPrincipal currentUser) {
        return ResponseEntity.ok(ApiResponse.success(
                "Profile retrieved successfully", authService.getCurrentUser(currentUser.getId())));
    }

    @PutMapping(value = "/me", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Update the signed-in caller's profile")
    public ResponseEntity<?> updateCurrentUser(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @RequestParam(value = "fullName", required = false) String fullName,
            @RequestParam(value = "email", required = false) String email,
            @RequestParam(value = "gender", required = false) String gender,
            @RequestParam(value = "avatar", required = false) MultipartFile avatarFile
    ) {
        UserSummary updated =
                authService.updateCurrentUser(currentUser.getId(), fullName, email, gender, avatarFile);
        return ResponseEntity.ok(ApiResponse.success("Profile updated successfully", updated));
    }

    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "List every user (admin only)")
    public ResponseEntity<?> getAllUsers() {
        return ResponseEntity.ok(ApiResponse.success("Users retrieved successfully", authService.listUsers()));
    }

    @GetMapping("/users/{id}")
    @Operation(summary = "Fetch one user", description = "Allowed for an admin, or for the user themselves")
    public ResponseEntity<?> getUserById(
            @PathVariable Long id, @AuthenticationPrincipal UserPrincipal currentUser) {
        UserSummary user = authService.getUser(id, currentUser.getId(), isAdmin(currentUser));
        return ResponseEntity.ok(ApiResponse.success("User retrieved successfully", user));
    }

    @PutMapping(value = "/users/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Update a user", description = "Allowed for an admin, or for the user themselves. Only an admin may change the role.")
    public ResponseEntity<?> updateUser(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal currentUser,
            @RequestParam(value = "fullName", required = false) String fullName,
            @RequestParam(value = "email", required = false) String email,
            @RequestParam(value = "role", required = false) String role,
            @RequestParam(value = "gender", required = false) String gender,
            @RequestParam(value = "active", required = false) Boolean active,
            @RequestParam(value = "avatar", required = false) MultipartFile avatarFile
    ) {
        UserSummary updated = authService.updateUser(
                id, currentUser.getId(), isAdmin(currentUser),
                fullName, email, role, gender, active, avatarFile);
        return ResponseEntity.ok(ApiResponse.success("User updated successfully", updated));
    }

    @DeleteMapping("/users/{id}")
    @Operation(summary = "Delete a user", description = "Allowed for an admin, or for the user themselves")
    public ResponseEntity<?> deleteUser(
            @PathVariable Long id, @AuthenticationPrincipal UserPrincipal currentUser) {
        authService.deleteUser(id, currentUser.getId(), isAdmin(currentUser));
        return ResponseEntity.ok(ApiResponse.success("User deleted successfully", null));
    }

    private boolean isAdmin(UserPrincipal principal) {
        return principal.getAuthorities().stream()
                .anyMatch(a -> Role.ROLE_ADMIN.name().equals(a.getAuthority()));
    }
}
