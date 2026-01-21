package com.musicapi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserPublicDto {
    private Long id;
    private String username;
    private String fullName;
    private String avatar;
}
