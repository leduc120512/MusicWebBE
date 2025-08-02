package com.musicapi.controller;

import com.musicapi.dto.*;
import com.musicapi.model.Role;
import com.musicapi.model.User;
import com.musicapi.repository.UserRepository;
import com.musicapi.security.JwtTokenProvider;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

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

            // Kiểm tra mật khẩu sử dụng PasswordEncoder
            if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("Invalid username/email or password"));
            }

            // Nếu hợp lệ thì tạo Authentication object
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getUsername(), loginRequest.getPassword())
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = tokenProvider.generateToken(authentication);

            UserSummary userSummary = new UserSummary(
                    user.getId(),
                    user.getUsername(),
                    user.getFullName(),
                    user.getEmail(),
                    user.getAvatar(),
                    user.getRole()
            );

            return ResponseEntity.ok(ApiResponse.success("Login successful", new JwtAuthenticationResponse(jwt, userSummary)));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("An error occurred: " + e.getMessage()));
        }
    }


    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpRequest signUpRequest) {
        // Kiểm tra trùng username
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Username is already taken!"));
        }

        // Kiểm tra trùng email
        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Email Address already in use!"));
        }

        // Tạo mới đối tượng User và gán thông tin
        User user = new User();
        user.setUsername(signUpRequest.getUsername());
        user.setEmail(signUpRequest.getEmail());
        user.setFullName(signUpRequest.getFullName());

        // ✅ Mã hóa mật khẩu
        String encodedPassword = passwordEncoder.encode(signUpRequest.getPassword());
        user.setPassword(encodedPassword);

        // Gán role mặc định
        user.setRole(Role.USER);

        // Lưu vào database
        User result = userRepository.save(user);

        // Trả về dữ liệu response
        UserSummary userSummary = new UserSummary(
                result.getId(),
                result.getUsername(),
                result.getFullName(),
                result.getEmail(),
                result.getAvatar(),
                result.getRole()
        );

        return ResponseEntity.ok(ApiResponse.success("User registered successfully", userSummary));
    }
    // Lấy danh sách tất cả người dùng
    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers(@RequestHeader("Authorization") String token) {
        try {
            Long userId = tokenProvider.getUserIdFromJWT(token.replace("Bearer ", ""));
            User requester = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            if (requester.getRole() != Role.ADMIN) {
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

            if (requester.getRole() != Role.ADMIN && !userId.equals(id)) {
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
    @PutMapping("/users/{id}")
    public ResponseEntity<?> updateUserById(@PathVariable Long id,
                                            @RequestHeader("Authorization") String token,
                                            @RequestBody Map<String, Object> updates) {
        try {
            Long requesterId = tokenProvider.getUserIdFromJWT(token.replace("Bearer ", ""));
            User requester = userRepository.findById(requesterId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            if (requester.getRole() != Role.ADMIN && !requesterId.equals(id)) {
                return ResponseEntity.status(403).body(ApiResponse.error("Access denied"));
            }

            User user = userRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            if (updates.containsKey("fullName")) user.setFullName((String) updates.get("fullName"));
            if (updates.containsKey("email")) user.setEmail((String) updates.get("email"));
            if (updates.containsKey("avatar")) user.setAvatar((String) updates.get("avatar"));
            if (updates.containsKey("role") && requester.getRole() == Role.ADMIN) {
                user.setRole(Role.valueOf((String) updates.get("role")));
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

            if (requester.getRole() != Role.ADMIN && !requesterId.equals(id)) {
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
    public ResponseEntity<?> getCurrentUser(@RequestHeader("Authorization") String token) {
        try {
            // Extract JWT token
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
                    user.getRole()
            );

            return ResponseEntity.ok(ApiResponse.success(userSummary));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Invalid token"));
        }
    }
}
