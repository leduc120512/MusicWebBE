package com.musicapi.service;

import com.musicapi.dto.ArtistProfileResponse;
import com.musicapi.dto.ArtistProfileUpdateRequest;
import com.musicapi.model.ArtistProfile;
import com.musicapi.model.Role;
import com.musicapi.model.User;
import com.musicapi.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ArtistProfileService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ArtistProfileRepository artistProfileRepository;

    @Autowired
    private SongRepository songRepository;

    @Autowired
    private AlbumRepository albumRepository;

    @Autowired
    private FollowRepository followRepository;

    public ArtistProfileResponse getPublicArtistProfile(Long artistId) {
        User artist = getArtistByIdOrThrow(artistId);
        ArtistProfile profile = artistProfileRepository.findByArtistId(artistId).orElse(null);
        return mapToResponse(artist, profile);
    }

    public ArtistProfileResponse getMyArtistProfile(Long currentUserId) {
        User artist = getArtistByIdOrThrow(currentUserId);
        ensureArtistRole(artist);
        ArtistProfile profile = artistProfileRepository.findByArtist(artist).orElseGet(() -> createDefaultProfile(artist));
        return mapToResponse(artist, profile);
    }

    @Transactional
    public ArtistProfileResponse upsertMyArtistProfile(Long currentUserId, ArtistProfileUpdateRequest request) {
        User artist = getArtistByIdOrThrow(currentUserId);
        ensureArtistRole(artist);

        ArtistProfile profile = artistProfileRepository.findByArtist(artist).orElseGet(() -> createDefaultProfile(artist));
        profile.setStageName(request.getStageName());
        profile.setBio(request.getBio());
        profile.setCoverImage(request.getCoverImage());
        profile.setSocialLinks(request.getSocialLinks());

        ArtistProfile saved = artistProfileRepository.save(profile);
        return mapToResponse(artist, saved);
    }

    private ArtistProfile createDefaultProfile(User artist) {
        ArtistProfile profile = new ArtistProfile();
        profile.setArtist(artist);
        profile.setStageName(artist.getFullName());
        return profile;
    }

    private User getArtistByIdOrThrow(Long artistId) {
        return userRepository.findById(artistId)
                .orElseThrow(() -> new RuntimeException("Artist not found"));
    }

    private void ensureArtistRole(User user) {
        if (user.getRole() != Role.ROLE_AUTHOR && user.getRole() != Role.ROLE_ADMIN) {
            throw new RuntimeException("Only artists can update artist profile");
        }
    }

    private ArtistProfileResponse mapToResponse(User artist, ArtistProfile profile) {
        ArtistProfileResponse response = new ArtistProfileResponse();
        response.setArtistId(artist.getId());
        response.setUsername(artist.getUsername());
        response.setFullName(artist.getFullName());
        response.setAvatar(artist.getAvatar());

        response.setStageName(profile != null ? profile.getStageName() : artist.getFullName());
        response.setBio(profile != null ? profile.getBio() : null);
        response.setCoverImage(profile != null ? profile.getCoverImage() : null);
        response.setSocialLinks(profile != null ? profile.getSocialLinks() : null);
        response.setProfileUpdatedAt(profile != null ? profile.getUpdatedAt() : null);

        response.setTotalSongs(songRepository.countByArtist(artist));
        response.setTotalAlbums(albumRepository.countByArtist(artist));
        response.setFollowerCount(followRepository.countFollowersByUser(artist));
        response.setTotalPlays(songRepository.sumPlayCountByArtist(artist));
        return response;
    }
}

