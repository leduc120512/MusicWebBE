package com.musicapi.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.musicapi.dto.UserRecommendationResponse;
import com.musicapi.model.PlayHistory;
import com.musicapi.model.Role;
import com.musicapi.model.Song;
import com.musicapi.model.User;
import com.musicapi.repository.FollowRepository;
import com.musicapi.repository.PlayHistoryRepository;
import com.musicapi.repository.SongRepository;
import com.musicapi.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserRecommendationService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PlayHistoryRepository playHistoryRepository;

    @Autowired
    private SongRepository songRepository;

    @Autowired
    private FollowRepository followRepository;

    @Value("${ollama.base-url:http://localhost:11434}")
    private String ollamaBaseUrl;

    @Value("${ollama.user-recommendation-model:llama3.1}")
    private String defaultModel;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final RestTemplate restTemplate;

    public UserRecommendationService() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(10000);
        factory.setReadTimeout(45000);
        this.restTemplate = new RestTemplate(factory);
    }

    @Transactional
    public List<UserRecommendationResponse> recommendUsers(Long userId, int limit, String model) {
        User currentUser = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        int safeLimit = Math.min(Math.max(limit, 1), 20);

        List<PlayHistory> history = playHistoryRepository.findRecentHistoryWithSongByUser(
                currentUser,
                PageRequest.of(0, 80)
        );

        Map<Long, CandidateScore> candidates = buildCandidates(currentUser, history);
        if (candidates.isEmpty()) {
            addPopularAuthors(currentUser, candidates);
        }

        List<UserRecommendationResponse> fallback = candidates.values().stream()
                .sorted(Comparator.comparing(CandidateScore::score).reversed())
                .limit(safeLimit)
                .map(candidate -> toResponse(currentUser, candidate, false, candidate.reason()))
                .toList();

        Optional<List<UserRecommendationResponse>> ollama = rankWithOllama(currentUser, history, candidates, safeLimit, model);
        return ollama.orElse(fallback);
    }

    private Map<Long, CandidateScore> buildCandidates(User currentUser, List<PlayHistory> history) {
        Map<Long, CandidateScore> candidates = new LinkedHashMap<>();
        Map<String, Long> genreCounts = history.stream()
                .map(PlayHistory::getSong)
                .filter(song -> song.getGenre() != null && song.getGenre().getName() != null)
                .collect(Collectors.groupingBy(song -> song.getGenre().getName(), Collectors.counting()));

        for (PlayHistory item : history) {
            Song song = item.getSong();
            if (song == null || song.getArtist() == null) continue;

            User artist = song.getArtist();
            if (artist.getId().equals(currentUser.getId())) continue;
            if (artist.getRole() != Role.ROLE_AUTHOR && artist.getRole() != Role.ROLE_ADMIN) continue;

            CandidateScore candidate = candidates.computeIfAbsent(artist.getId(), id -> new CandidateScore(artist));
            candidate.matchedHistoryCount++;
            candidate.score += 8;
            candidate.reason = "Bạn nghe nhiều bài của tác giả này";
        }

        if (!genreCounts.isEmpty()) {
            List<Song> songs = songRepository.findAll();
            for (Song song : songs) {
                if (!song.isActive() || song.getArtist() == null || song.getGenre() == null) continue;
                User artist = song.getArtist();
                if (artist.getId().equals(currentUser.getId())) continue;
                if (artist.getRole() != Role.ROLE_AUTHOR && artist.getRole() != Role.ROLE_ADMIN) continue;

                Long genreWeight = genreCounts.get(song.getGenre().getName());
                if (genreWeight == null) continue;

                CandidateScore candidate = candidates.computeIfAbsent(artist.getId(), id -> new CandidateScore(artist));
                candidate.score += Math.min(genreWeight, 10);
                if (candidate.reason == null) {
                    candidate.reason = "Phù hợp với thể loại bạn thường nghe: " + song.getGenre().getName();
                }
            }
        }

        candidates.values().forEach(candidate -> {
            if (candidate.reason == null) {
                candidate.reason = "Tác giả phù hợp với lịch sử nghe của bạn";
            }
            candidate.score += Math.log10(Math.max(candidate.artist.getId(), 1L));
        });
        return candidates;
    }

    private void addPopularAuthors(User currentUser, Map<Long, CandidateScore> candidates) {
        userRepository.findActiveUsersByRole(Role.ROLE_AUTHOR).forEach(author -> {
            if (author.getId().equals(currentUser.getId())) return;
            CandidateScore candidate = candidates.computeIfAbsent(author.getId(), id -> new CandidateScore(author));
            candidate.score = Math.max(candidate.score, songRepository.sumPlayCountByArtist(author));
            candidate.reason = "Tác giả đang có nhiều lượt nghe";
        });
    }

    private Optional<List<UserRecommendationResponse>> rankWithOllama(
            User currentUser,
            List<PlayHistory> history,
            Map<Long, CandidateScore> candidates,
            int limit,
            String model
    ) {
        if (candidates.isEmpty()) return Optional.empty();

        String safeModel = model == null || model.isBlank() ? defaultModel : model.trim();
        String prompt = buildPrompt(history, candidates, limit);

        try {
            Map<String, Object> request = new HashMap<>();
            request.put("model", safeModel);
            request.put("prompt", prompt);
            request.put("stream", false);
            request.put("format", "json");

            @SuppressWarnings("unchecked")
            Map<String, Object> response = restTemplate.postForObject(
                    ollamaBaseUrl + "/api/generate",
                    request,
                    Map.class
            );

            String aiText = response == null || response.get("response") == null
                    ? ""
                    : response.get("response").toString();
            JsonNode root = objectMapper.readTree(extractJson(aiText));
            JsonNode items = root.path("recommendations");
            if (!items.isArray() || items.isEmpty()) return Optional.empty();

            List<UserRecommendationResponse> ranked = new ArrayList<>();
            for (JsonNode item : items) {
                Long userId = item.path("userId").asLong(0);
                CandidateScore candidate = candidates.get(userId);
                if (candidate == null) continue;

                String reason = item.path("reason").asText(candidate.reason());
                UserRecommendationResponse dto = toResponse(currentUser, candidate, true, reason);
                if (item.has("score")) {
                    dto.setScore(item.path("score").asDouble(dto.getScore()));
                }
                ranked.add(dto);
            }

            candidates.values().stream()
                    .filter(candidate -> ranked.stream().noneMatch(dto -> dto.getId().equals(candidate.artist.getId())))
                    .sorted(Comparator.comparing(CandidateScore::score).reversed())
                    .limit(Math.max(0, limit - ranked.size()))
                    .map(candidate -> toResponse(currentUser, candidate, true, candidate.reason()))
                    .forEach(ranked::add);

            return Optional.of(ranked.stream().limit(limit).toList());
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    private String buildPrompt(List<PlayHistory> history, Map<Long, CandidateScore> candidates, int limit) {
        String listened = history.stream()
                .limit(25)
                .map(item -> {
                    Song song = item.getSong();
                    String genre = song.getGenre() == null ? "unknown" : song.getGenre().getName();
                    String artist = song.getArtist() == null ? "unknown" : song.getArtist().getFullName();
                    return "- " + song.getTitle() + " | artist=" + artist + " | genre=" + genre;
                })
                .collect(Collectors.joining("\n"));

        String candidateText = candidates.values().stream()
                .sorted(Comparator.comparing(CandidateScore::score).reversed())
                .limit(30)
                .map(candidate -> "- userId=%d | name=%s | score=%.2f | matchedHistory=%d | reason=%s".formatted(
                        candidate.artist.getId(),
                        candidate.artist.getFullName(),
                        candidate.score(),
                        candidate.matchedHistoryCount,
                        candidate.reason()
                ))
                .collect(Collectors.joining("\n"));

        return """
                Bạn là hệ thống đề xuất user/tác giả cho ứng dụng nghe nhạc.
                Dựa trên lịch sử nghe và danh sách ứng viên, hãy chọn tối đa %d user phù hợp nhất.
                Ưu tiên tác giả cùng gu thể loại, tác giả mà user đã nghe nhiều, và tác giả có nội dung gần sở thích.
                Chỉ trả về JSON hợp lệ theo mẫu:
                {"recommendations":[{"userId":1,"score":95,"reason":"lý do ngắn"}]}

                Lịch sử nghe:
                %s

                Ứng viên:
                %s
                """.formatted(limit, listened.isBlank() ? "Chưa có lịch sử nghe" : listened, candidateText);
    }

    private UserRecommendationResponse toResponse(User currentUser, CandidateScore candidate, boolean ollamaUsed, String reason) {
        User artist = candidate.artist;
        UserRecommendationResponse response = new UserRecommendationResponse();
        response.setId(artist.getId());
        response.setUsername(artist.getUsername());
        response.setFullName(artist.getFullName());
        response.setAvatar(artist.getAvatar());
        response.setRole(artist.getRole());
        response.setFollowerCount(followRepository.countFollowersByUser(artist));
        response.setTotalSongs(songRepository.countByArtist(artist));
        response.setTotalPlays(songRepository.sumPlayCountByArtist(artist));
        response.setMatchedHistoryCount(candidate.matchedHistoryCount);
        response.setScore(candidate.score());
        response.setReason(reason);
        response.setFollowing(followRepository.existsByFollowerAndFollowing(currentUser, artist));
        response.setOllamaUsed(ollamaUsed);
        return response;
    }

    private String extractJson(String text) {
        int start = text.indexOf('{');
        int end = text.lastIndexOf('}');
        if (start >= 0 && end > start) {
            return text.substring(start, end + 1);
        }
        return "{\"recommendations\":[]}";
    }

    private static class CandidateScore {
        private final User artist;
        private long matchedHistoryCount = 0;
        private double score = 0;
        private String reason;

        private CandidateScore(User artist) {
            this.artist = artist;
        }

        private double score() {
            return score;
        }

        private String reason() {
            return reason == null ? "Phù hợp với lịch sử nghe của bạn" : reason;
        }
    }
}
