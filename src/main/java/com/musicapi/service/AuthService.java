package com.musicapi.service;

import com.musicapi.dto.JwtAuthenticationResponse;
import com.musicapi.dto.LoginRequest;
import com.musicapi.dto.SignUpRequest;
import com.musicapi.dto.UserSummary;
import com.musicapi.model.Gender;
import com.musicapi.model.Role;
import com.musicapi.model.User;
import com.musicapi.repository.UserRepository;
import com.musicapi.security.JwtTokenProvider;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

/**
 * Everything the auth endpoints do, so AuthController stays a thin HTTP layer.
 *
 * Failures are signalled with {@link ResponseStatusException}; GlobalExceptionHandler
 * turns those into the standard envelope with the right status.
 */
@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;
    private final FileStorageService fileStorageService;

    public AuthService(
            AuthenticationManager authenticationManager,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtTokenProvider tokenProvider,
            FileStorageService fileStorageService
    ) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
        this.fileStorageService = fileStorageService;
    }

    // ------------------------------------------------------------------ sign in

    @Transactional(readOnly = true)
    public JwtAuthenticationResponse signIn(LoginRequest request) {
        User user = userRepository
                .findByUsernameOrEmail(request.getUsernameOrEmail(), request.getUsernameOrEmail())
                .orElseThrow(AuthService::invalidCredentials);

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw invalidCredentials();
        }

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getUsername(), request.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        return new JwtAuthenticationResponse(tokenProvider.generateToken(authentication), UserSummary.from(user));
    }

    /** Deliberately identical for "no such user" and "wrong password". */
    private static ResponseStatusException invalidCredentials() {
        return new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username/email or password");
    }

    // ------------------------------------------------------------------ sign up

    @Transactional
    public UserSummary signUp(SignUpRequest request, MultipartFile avatarFile) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username is already taken");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email is already in use");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setFullName(request.getFullName());
        user.setGender(parseGender(request.getGender()));
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.ROLE_USER);

        String avatar = fileStorageService.store(avatarFile, "userImg");
        if (avatar != null) {
            user.setAvatar(avatar);
        }

        return UserSummary.from(userRepository.save(user));
    }

    // -------------------------------------------------------------- admin views

    public List<UserSummary> listUsers() {
        return userRepository.findAll().stream().map(UserSummary::from).toList();
    }

    /**
     * @param requesterId  caller's id
     * @param requesterIsAdmin whether the caller holds ROLE_ADMIN
     */
    @Transactional(readOnly = true)
    public UserSummary getUser(Long id, Long requesterId, boolean requesterIsAdmin) {
        requireSelfOrAdmin(id, requesterId, requesterIsAdmin);
        return UserSummary.from(requireUser(id));
    }

    @Transactional
    public UserSummary updateUser(
            Long id,
            Long requesterId,
            boolean requesterIsAdmin,
            String fullName,
            String email,
            String role,
            String gender,
            Boolean active,
            MultipartFile avatarFile
    ) {
        requireSelfOrAdmin(id, requesterId, requesterIsAdmin);

        User user = requireUser(id);
        applyProfileFields(user, fullName, email, gender);

        if (active != null) {
            user.setActive(active);
        }

        // Only an admin may change a role, whoever the target is
        if (role != null && !role.isBlank() && requesterIsAdmin) {
            user.setRole(parseRole(role));
        }

        String avatar = fileStorageService.store(avatarFile, "userImg");
        if (avatar != null) {
            user.setAvatar(avatar);
        }

        return UserSummary.from(userRepository.save(user));
    }

    @Transactional
    public void deleteUser(Long id, Long requesterId, boolean requesterIsAdmin) {
        requireSelfOrAdmin(id, requesterId, requesterIsAdmin);
        if (!userRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        userRepository.deleteById(id);
    }

    // ------------------------------------------------------------- current user

    @Transactional(readOnly = true)
    public UserSummary getCurrentUser(Long userId) {
        return UserSummary.from(requireUser(userId));
    }

    @Transactional
    public UserSummary updateCurrentUser(
            Long userId, String fullName, String email, String gender, MultipartFile avatarFile) {

        User user = requireUser(userId);
        applyProfileFields(user, fullName, email, gender);

        String avatar = fileStorageService.store(avatarFile, "userImg");
        if (avatar != null) {
            user.setAvatar(avatar);
        }

        return UserSummary.from(userRepository.save(user));
    }

    // ----------------------------------------------------------------- helpers

    private void applyProfileFields(User user, String fullName, String email, String gender) {
        if (fullName != null && !fullName.isBlank()) {
            user.setFullName(fullName);
        }

        if (email != null && !email.isBlank()) {
            Optional<User> owner = userRepository.findByEmail(email);
            if (owner.isPresent() && !owner.get().getId().equals(user.getId())) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Email is already in use");
            }
            user.setEmail(email);
        }

        if (gender != null && !gender.isBlank()) {
            user.setGender(parseGender(gender));
        }
    }

    private User requireUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }

    private void requireSelfOrAdmin(Long targetId, Long requesterId, boolean requesterIsAdmin) {
        if (!requesterIsAdmin && !targetId.equals(requesterId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }
    }

    /** Unrecognised values fall back to OTHER rather than failing the request. */
    private Gender parseGender(String value) {
        if (value == null || value.isBlank()) {
            return Gender.OTHER;
        }
        try {
            return Gender.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            return Gender.OTHER;
        }
    }

    /** Accepts both "ADMIN" and "ROLE_ADMIN". */
    private Role parseRole(String value) {
        String normalised = value.trim().toUpperCase();
        if (!normalised.startsWith("ROLE_")) {
            normalised = "ROLE_" + normalised;
        }
        try {
            return Role.valueOf(normalised);
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Role must be one of USER, AUTHOR, ADMIN");
        }
    }
}
