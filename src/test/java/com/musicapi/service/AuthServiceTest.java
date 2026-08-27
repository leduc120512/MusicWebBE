package com.musicapi.service;

import com.musicapi.dto.LoginRequest;
import com.musicapi.dto.SignUpRequest;
import com.musicapi.dto.UserSummary;
import com.musicapi.model.Gender;
import com.musicapi.model.Role;
import com.musicapi.model.User;
import com.musicapi.repository.UserRepository;
import com.musicapi.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Rules that used to live inline in AuthController, now covered directly.
 */
class AuthServiceTest {

    private UserRepository userRepository;
    private AuthService authService;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        authService = new AuthService(
                mock(AuthenticationManager.class),
                userRepository,
                passwordEncoder,
                mock(JwtTokenProvider.class),
                mock(FileStorageService.class));
    }

    private User user(long id, String username, String rawPassword, Role role) {
        User user = new User();
        user.setId(id);
        user.setUsername(username);
        user.setEmail(username + "@example.com");
        user.setFullName(username);
        user.setGender(Gender.OTHER);
        user.setRole(role);
        user.setPassword(passwordEncoder.encode(rawPassword));
        return user;
    }

    private static void assertStatus(Throwable thrown, HttpStatus expected) {
        assertThat(thrown).isInstanceOf(ResponseStatusException.class);
        assertThat(((ResponseStatusException) thrown).getStatusCode()).isEqualTo(expected);
    }

    // ------------------------------------------------------------------ sign in

    @Test
    @DisplayName("an unknown account and a wrong password fail identically with 401")
    void badCredentialsAreIndistinguishable() {
        LoginRequest request = new LoginRequest();
        request.setUsernameOrEmail("nobody");
        request.setPassword("whatever");

        when(userRepository.findByUsernameOrEmail(anyString(), anyString())).thenReturn(Optional.empty());
        Throwable unknownUser = org.assertj.core.api.Assertions.catchThrowable(() -> authService.signIn(request));

        when(userRepository.findByUsernameOrEmail(anyString(), anyString()))
                .thenReturn(Optional.of(user(1L, "nobody", "the-real-one", Role.ROLE_USER)));
        Throwable wrongPassword = org.assertj.core.api.Assertions.catchThrowable(() -> authService.signIn(request));

        assertStatus(unknownUser, HttpStatus.UNAUTHORIZED);
        assertStatus(wrongPassword, HttpStatus.UNAUTHORIZED);
        assertThat(((ResponseStatusException) unknownUser).getReason())
                .isEqualTo(((ResponseStatusException) wrongPassword).getReason())
                .isEqualTo("Invalid username/email or password");
    }

    // ------------------------------------------------------------------ sign up

    @Test
    @DisplayName("a duplicate username is 409, and nothing is saved")
    void duplicateUsernameIsRejected() {
        SignUpRequest request = new SignUpRequest();
        request.setUsername("taken");
        request.setEmail("taken@example.com");
        request.setFullName("Taken");
        request.setPassword("123456");

        when(userRepository.existsByUsername("taken")).thenReturn(true);

        assertStatus(org.assertj.core.api.Assertions.catchThrowable(() -> authService.signUp(request, null)),
                HttpStatus.CONFLICT);
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("the stored password is hashed, never the raw string")
    void signUpHashesThePassword() {
        SignUpRequest request = new SignUpRequest();
        request.setUsername("fresh");
        request.setEmail("fresh@example.com");
        request.setFullName("Fresh");
        request.setPassword("s3cret!");
        request.setGender("male");

        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(i -> {
            User saved = i.getArgument(0);
            saved.setId(99L);
            return saved;
        });

        UserSummary created = authService.signUp(request, null);

        assertThat(created.getId()).isEqualTo(99L);
        assertThat(created.getRole()).isEqualTo(Role.ROLE_USER);
        assertThat(created.getGender()).isEqualTo(Gender.MALE);

        org.mockito.ArgumentCaptor<User> captor = org.mockito.ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        assertThat(captor.getValue().getPassword()).isNotEqualTo("s3cret!").startsWith("$2");
    }

    @Test
    @DisplayName("an unrecognised gender falls back to OTHER instead of failing")
    void unknownGenderFallsBack() {
        SignUpRequest request = new SignUpRequest();
        request.setUsername("odd");
        request.setEmail("odd@example.com");
        request.setFullName("Odd");
        request.setPassword("123456");
        request.setGender("banana");

        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        assertThat(authService.signUp(request, null).getGender()).isEqualTo(Gender.OTHER);
    }

    // ------------------------------------------------------------ authorisation

    @Test
    @DisplayName("a member cannot read another member's account")
    void memberCannotReadSomeoneElse() {
        assertStatus(org.assertj.core.api.Assertions.catchThrowable(
                () -> authService.getUser(2L, 1L, false)), HttpStatus.FORBIDDEN);
    }

    @Test
    @DisplayName("an admin can read any account")
    void adminCanReadAnyone() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(user(2L, "someone", "x", Role.ROLE_USER)));

        assertThat(authService.getUser(2L, 1L, true).getUsername()).isEqualTo("someone");
    }

    @Test
    @DisplayName("a missing account is 404, not 400")
    void missingUserIs404() {
        when(userRepository.findById(7L)).thenReturn(Optional.empty());

        assertStatus(org.assertj.core.api.Assertions.catchThrowable(
                () -> authService.getUser(7L, 7L, false)), HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("a non-admin cannot promote themselves")
    void roleChangesAreAdminOnly() {
        User self = user(3L, "member", "x", Role.ROLE_USER);
        when(userRepository.findById(3L)).thenReturn(Optional.of(self));
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        UserSummary updated = authService.updateUser(
                3L, 3L, false, null, null, "ADMIN", null, null, null);

        assertThat(updated.getRole()).isEqualTo(Role.ROLE_USER);
    }

    @Test
    @DisplayName("deleting an account that does not exist is 404")
    void deletingAMissingUserIs404() {
        when(userRepository.existsById(5L)).thenReturn(false);

        assertStatus(org.assertj.core.api.Assertions.catchThrowable(
                () -> authService.deleteUser(5L, 5L, false)), HttpStatus.NOT_FOUND);
        verify(userRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("claiming an email that belongs to somebody else is 409")
    void duplicateEmailOnUpdateIsRejected() {
        User self = user(3L, "member", "x", Role.ROLE_USER);
        User other = user(4L, "other", "x", Role.ROLE_USER);

        when(userRepository.findById(3L)).thenReturn(Optional.of(self));
        when(userRepository.findByEmail("other@example.com")).thenReturn(Optional.of(other));

        assertStatus(org.assertj.core.api.Assertions.catchThrowable(
                        () -> authService.updateCurrentUser(3L, null, "other@example.com", null, null)),
                HttpStatus.CONFLICT);
    }
}
