package com.musicapi.dto;

import com.musicapi.model.Gender;
import com.musicapi.model.Role;
import com.musicapi.model.User;

public class UserSummary {
    private Long id;
    private String username;
    private String fullName;
    private String email;
    private String avatar;
    private Gender gender;
    private Role role;

    public UserSummary(Long id, String username, String fullName, String email, String avatar, Gender gender, Role role) {
        this.id = id;
        this.username = username;
        this.fullName = fullName;
        this.email = email;
        this.avatar = avatar;
        this.gender = gender;
        this.role = role;
    }

    /**
     * The only shape a User is ever exposed in. Keeps the password, the
     * relations and the audit columns out of every response.
     */
    public static UserSummary from(User user) {
        return new UserSummary(
                user.getId(),
                user.getUsername(),
                user.getFullName(),
                user.getEmail(),
                user.getAvatar(),
                user.getGender(),
                user.getRole()
        );
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getAvatar() { return avatar; }
    public void setAvatar(String avatar) { this.avatar = avatar; }

    public Gender getGender() { return gender; }
    public void setGender(Gender gender) { this.gender = gender; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
}
