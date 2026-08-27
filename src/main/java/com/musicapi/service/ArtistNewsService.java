package com.musicapi.service;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import com.musicapi.dto.ArtistNewsRequest;
import com.musicapi.dto.ArtistNewsResponse;
import com.musicapi.model.ArtistNews;
import com.musicapi.model.Role;
import com.musicapi.model.User;
import com.musicapi.repository.ArtistNewsRepository;
import com.musicapi.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ArtistNewsService {

    private final ArtistNewsRepository artistNewsRepository;
    private final UserRepository userRepository;

    public ArtistNewsService(ArtistNewsRepository artistNewsRepository, UserRepository userRepository) {
        this.artistNewsRepository = artistNewsRepository;
        this.userRepository = userRepository;
    }

    public Page<ArtistNewsResponse> getPublicNews(Long artistId, Pageable pageable) {
        User artist = getArtistOrThrow(artistId);
        return artistNewsRepository.findByArtistAndPublishedTrueOrderByCreatedAtDesc(artist, pageable)
                .map(this::mapToResponse);
    }

    public ArtistNewsResponse getPublicNewsDetail(Long artistId, Long newsId) {
        User artist = getArtistOrThrow(artistId);
        ArtistNews news = artistNewsRepository.findByIdAndArtistAndPublishedTrue(newsId, artist)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "News not found"));
        return mapToResponse(news);
    }

    public Page<ArtistNewsResponse> getMyNews(Long currentUserId, Pageable pageable) {
        User artist = getArtistOrThrow(currentUserId);
        ensureArtistRole(artist);
        return artistNewsRepository.findByArtistOrderByCreatedAtDesc(artist, pageable)
                .map(this::mapToResponse);
    }

    @Transactional
    public ArtistNewsResponse createMyNews(Long currentUserId, ArtistNewsRequest request) {
        User artist = getArtistOrThrow(currentUserId);
        ensureArtistRole(artist);

        ArtistNews news = new ArtistNews();
        news.setArtist(artist);
        news.setTitle(request.getTitle());
        news.setContent(request.getContent());
        news.setThumbnail(request.getThumbnail());
        news.setPublished(request.getPublished() == null || request.getPublished());

        return mapToResponse(artistNewsRepository.save(news));
    }

    @Transactional
    public ArtistNewsResponse updateMyNews(Long currentUserId, Long newsId, ArtistNewsRequest request) {
        User artist = getArtistOrThrow(currentUserId);
        ensureArtistRole(artist);

        ArtistNews news = artistNewsRepository.findByIdAndArtist(newsId, artist)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "News not found"));
        news.setTitle(request.getTitle());
        news.setContent(request.getContent());
        news.setThumbnail(request.getThumbnail());
        if (request.getPublished() != null) {
            news.setPublished(request.getPublished());
        }

        return mapToResponse(artistNewsRepository.save(news));
    }

    @Transactional
    public void deleteMyNews(Long currentUserId, Long newsId) {
        User artist = getArtistOrThrow(currentUserId);
        ensureArtistRole(artist);

        ArtistNews news = artistNewsRepository.findByIdAndArtist(newsId, artist)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "News not found"));
        artistNewsRepository.delete(news);
    }

    private User getArtistOrThrow(Long artistId) {
        return userRepository.findById(artistId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Artist not found"));
    }

    private void ensureArtistRole(User user) {
        if (user.getRole() != Role.ROLE_AUTHOR && user.getRole() != Role.ROLE_ADMIN) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only artists can manage artist news");
        }
    }

    private ArtistNewsResponse mapToResponse(ArtistNews news) {
        ArtistNewsResponse response = new ArtistNewsResponse();
        response.setId(news.getId());
        response.setArtistId(news.getArtist().getId());
        response.setArtistName(news.getArtist().getFullName());
        response.setTitle(news.getTitle());
        response.setContent(news.getContent());
        response.setThumbnail(news.getThumbnail());
        response.setPublished(news.isPublished());
        response.setCreatedAt(news.getCreatedAt());
        response.setUpdatedAt(news.getUpdatedAt());
        return response;
    }
}

