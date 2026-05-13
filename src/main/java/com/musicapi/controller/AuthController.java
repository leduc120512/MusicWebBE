package com.musicapi.controller;

import com.musicapi.dto.*;
import com.musicapi.model.Gender;
import com.musicapi.model.Role;
import com.musicapi.model.User;
import com.musicapi.repository.UserRepository;
import com.musicapi.security.JwtTokenProvider;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Optional;
import java.util.UUID;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            Optional<User> userOptional = userRepository.findByUsernameOrEmail(
                    loginRequest.getUsernameOrEmail(),
                    loginRequest.getUsernameOrEmail()
            );

            if (userOptional.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("Invalid username/email or password"));
            }

            User user = userOptional.get();

            // so sánh trực tiếp mật khẩu người dùng nhập và mật khẩu lưu DB
            if (!loginRequest.getPassword().equals(user.getPassword())) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("Invalid username/email or password"));
            }

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            user.getUsername(),
                            loginRequest.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = tokenProvider.generateToken(authentication);

            UserSummary userSummary = new UserSummary(
                    user.getId(),
                    user.getUsername(),
                    user.getFullName(),
                    user.getEmail(),
                    user.getAvatar(),
                    user.getGender(),
                    user.getRole()
            );

            return ResponseEntity.ok(
                    ApiResponse.success(
                            "Login successful",
                            new JwtAuthenticationResponse(jwt, userSummary)
                    )
            );
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("An error occurred: " + e.getMessage()));
        }
    }


    @PostMapping(value = "/signup", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> registerUser(
            @RequestParam("username") String username,
            @RequestParam("email") String email,
            @RequestParam("fullName") String fullName,
            @RequestParam("password") String password,
            @RequestParam(value = "gender", required = false) String gender,
            @RequestParam(value = "avatar", required = false) MultipartFile avatarFile
    ) {
        try {
            if (userRepository.existsByUsername(username)) {
                return ResponseEntity.badRequest().body(ApiResponse.error("Username is already taken!"));
            }

            if (userRepository.existsByEmail(email)) {
                return ResponseEntity.badRequest().body(ApiResponse.error("Email already in use!"));
            }

            User user = new User();
            user.setUsername(username);
            user.setEmail(email);
            user.setFullName(fullName);
            user.setGender(parseGenderOrDefault(gender));

            // lưu thẳng mật khẩu
            user.setPassword(password);

            user.setRole(Role.ROLE_USER);

            if (avatarFile != null && !avatarFile.isEmpty()) {
                String uploadDir = "D:/web nhac/duan1/upload/userImg";
                String fileName = UUID.randomUUID() + "_" + avatarFile.getOriginalFilename();
                Path filePath = Paths.get(uploadDir, fileName);
                Files.createDirectories(filePath.getParent());
                Files.copy(avatarFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
                user.setAvatar("/upload/userImg/" + fileName);
            }

            User result = userRepository.save(user);

            UserSummary userSummary = new UserSummary(
                    result.getId(),
                    result.getUsername(),
                    result.getFullName(),
                    result.getEmail(),
                    result.getAvatar(),
                    result.getGender(),
                    result.getRole()
            );

            return ResponseEntity.ok(ApiResponse.success("User registered successfully", userSummary));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Signup failed: " + e.getMessage()));
        }
    }


    // Lấy danh sách tất cả người dùng
    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers(@RequestHeader("Authorization") String token) {
        try {
            Long userId = tokenProvider.getUserIdFromJWT(token.replace("Bearer ", ""));
            User requester = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            if (requester.getRole() != Role.ROLE_ADMIN) {
                return ResponseEntity.status(403).body(ApiResponse.error("Access denied"));
            }

            return ResponseEntity.ok(ApiResponse.success(userRepository.findAll()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to get users: " + e.getMessage()));
        }
    }

    // Lấy chi tiết người dùng theo ID
    @GetMapping("/users/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id,
                                         @RequestHeader("Authorization") String token) {
        try {
            Long userId = tokenProvider.getUserIdFromJWT(token.replace("Bearer ", ""));
            User requester = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            if (requester.getRole() != Role.ROLE_ADMIN && !userId.equals(id)) {
                return ResponseEntity.status(403).body(ApiResponse.error("Access denied"));
            }

            User user = userRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            return ResponseEntity.ok(ApiResponse.success(user));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to get user: " + e.getMessage()));
        }
    }

    // Cập nhật thông tin người dùng theo ID (admin)
    @PutMapping(value = "/users/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateUserWithAvatar(
            @PathVariable Long id,
            @RequestHeader("Authorization") String token,
            @RequestParam(value = "fullName", required = false) String fullName,
            @RequestParam(value = "email", required = false) String email,
            @RequestParam(value = "role", required = false) String role,
            @RequestParam(value = "gender", required = false) String gender,
            @RequestParam(value = "locked", required = false) Boolean locked,
            @RequestParam(value = "avatar", required = false) MultipartFile avatarFile
    ) {
        try {
            Long requesterId = tokenProvider.getUserIdFromJWT(token.replace("Bearer ", ""));
            User requester = userRepository.findById(requesterId).orElseThrow(() -> new RuntimeException("User not found"));

            if (requester.getRole() != Role.ROLE_ADMIN && !requesterId.equals(id)) {
                return ResponseEntity.status(403).body(ApiResponse.error("Access denied"));
            }

            User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));

            if (fullName != null) user.setFullName(fullName);
            if (email != null) user.setEmail(email);
            if (gender != null && !gender.isBlank()) user.setGender(parseGenderOrDefault(gender));
            if (locked != null) user.setActive(locked);

            if (role != null && requester.getRole() == Role.ROLE_ADMIN) {
                Role parsedRole = Role.valueOf(role.startsWith("ROLE_") ? role : "ROLE_" + role.toUpperCase());
                user.setRole(parsedRole);
            }

            if (avatarFile != null && !avatarFile.isEmpty()) {
                String uploadDir = "D:/web nhac/duan1/upload/userImg";
                Files.createDirectories(Paths.get(uploadDir));

                String avatarFileName = UUID.randomUUID() + "_" + avatarFile.getOriginalFilename();
                Path avatarPath = Paths.get(uploadDir, avatarFileName);
                Files.copy(avatarFile.getInputStream(), avatarPath, StandardCopyOption.REPLACE_EXISTING);

                user.setAvatar("/upload/userImg/" + avatarFileName);
            }

            userRepository.save(user);
            return ResponseEntity.ok(ApiResponse.success("User updated successfully"));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Update failed: " + e.getMessage()));
        }
    }


    // Xóa người dùng theo ID (admin)
    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUserById(@PathVariable Long id,
                                            @RequestHeader("Authorization") String token) {
        try {
            Long requesterId = tokenProvider.getUserIdFromJWT(token.replace("Bearer ", ""));
            User requester = userRepository.findById(requesterId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            if (requester.getRole() != Role.ROLE_ADMIN && !requesterId.equals(id)) {
                return ResponseEntity.status(403).body(ApiResponse.error("Access denied"));
            }

            if (!userRepository.existsById(id)) {
                return ResponseEntity.badRequest().body(ApiResponse.error("User not found"));
            }

            userRepository.deleteById(id);
            return ResponseEntity.ok(ApiResponse.success("User deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Delete failed: " + e.getMessage()));
        }
    }


    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@RequestHeader(value = "Authorization", required = false) String token) {
        try {
            if (token == null || token.isBlank() || !token.startsWith("Bearer ")) {
                return ResponseEntity.status(401).body(ApiResponse.error("Authorization header is missing or invalid"));
            }

            String jwt = token.replace("Bearer ", "");
            Long userId = tokenProvider.getUserIdFromJWT(jwt);

            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            UserSummary userSummary = new UserSummary(
                    user.getId(),
                    user.getUsername(),
                    user.getFullName(),
                    user.getEmail(),
                    user.getAvatar(),
                    user.getGender(),
                    user.getRole()
            );

            return ResponseEntity.ok(ApiResponse.success(userSummary));
        } catch (Exception e) {
            return ResponseEntity.status(401)
                    .body(ApiResponse.error("Invalid token"));
        }
    }

    @PutMapping(value = "/me", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateCurrentUser(
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestParam(value = "fullName", required = false) String fullName,
            @RequestParam(value = "email", required = false) String email,
            @RequestParam(value = "gender", required = false) String gender,
            @RequestParam(value = "avatar", required = false) MultipartFile avatarFile
    ) {
        try {
            if (token == null || token.isBlank() || !token.startsWith("Bearer ")) {
                return ResponseEntity.status(401).body(ApiResponse.error("Authorization header is missing or invalid"));
            }

            String jwt = token.replace("Bearer ", "");
            Long userId = tokenProvider.getUserIdFromJWT(jwt);

            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            if (fullName != null) user.setFullName(fullName);

            if (email != null && !email.isBlank()) {
                Optional<User> existingUser = userRepository.findByEmail(email);
                if (existingUser.isPresent() && !existingUser.get().getId().equals(userId)) {
                    return ResponseEntity.badRequest().body(ApiResponse.error("Email already in use!"));
                }
                user.setEmail(email);
            }

            if (gender != null && !gender.isBlank()) {
                user.setGender(parseGenderOrDefault(gender));
            }

            if (avatarFile != null && !avatarFile.isEmpty()) {
                String uploadDir = "D:/web nhac/duan1/upload/userImg";
                Files.createDirectories(Paths.get(uploadDir));

                String avatarFileName = UUID.randomUUID() + "_" + avatarFile.getOriginalFilename();
                Path avatarPath = Paths.get(uploadDir, avatarFileName);
                Files.copy(avatarFile.getInputStream(), avatarPath, StandardCopyOption.REPLACE_EXISTING);

                user.setAvatar("/upload/userImg/" + avatarFileName);
            }

            User result = userRepository.save(user);

            UserSummary userSummary = new UserSummary(
                    result.getId(),
                    result.getUsername(),
                    result.getFullName(),
                    result.getEmail(),
                    result.getAvatar(),
                    result.getGender(),
                    result.getRole()
            );

            return ResponseEntity.ok(ApiResponse.success("Profile updated successfully", userSummary));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Update profile failed: " + e.getMessage()));
        }
    }

    private Gender parseGenderOrDefault(String genderText) {
        if (genderText == null || genderText.isBlank()) {
            return Gender.OTHER;
        }
        try {
            return Gender.valueOf(genderText.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            return Gender.OTHER;
        }
    }
}
