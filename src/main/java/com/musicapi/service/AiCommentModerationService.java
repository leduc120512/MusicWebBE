package com.musicapi.service;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.musicapi.dto.AiCommentModerationResult;
import com.musicapi.dto.AiCommentModerationScanResponse;
import com.musicapi.model.Comment;
import com.musicapi.model.Role;
import com.musicapi.model.User;
import com.musicapi.repository.CommentRepository;
import com.musicapi.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AiCommentModerationService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;


    @Value("${ollama.base-url:http://localhost:11434}")
    private String ollamaBaseUrl;

    @Value("${ollama.comment-moderation-model:llama3.1}")
    private String defaultModel;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final RestTemplate restTemplate;

    public AiCommentModerationService(CommentRepository commentRepository, UserRepository userRepository) {
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;

        // Ollama can be slow to answer, so this client gets its own timeouts
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(15000);
        factory.setReadTimeout(45000);
        this.restTemplate = new RestTemplate(factory);
    }

    @Transactional(readOnly = true)
    public AiCommentModerationScanResponse scanAndDelete(Long adminId, int limit, String model) {
        User admin = userRepository.findById(adminId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        if (admin.getRole() != Role.ROLE_ADMIN) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only an administrator can run the comment scan");
        }

        int safeLimit = Math.min(Math.max(limit, 1), 100);
        String safeModel = model == null || model.isBlank() ? defaultModel : model.trim();
        List<Comment> comments = commentRepository.findByDeletedFalseOrderByCreatedAtDesc(PageRequest.of(0, safeLimit)).getContent();

        List<AiCommentModerationResult> results = new ArrayList<>();
        int violations = 0;
        int deleted = 0;

        for (Comment comment : comments) {
            AiCommentModerationResult result = moderate(comment, safeModel);
            if (result.isViolates()) {
                violations++;
                comment.setDeleted(true);
                comment.setAiModerationReason(result.getReason());
                comment.setAiModeratedAt(LocalDateTime.now());
                comment.setContent("[Comment removed for violating the community guidelines]");
                commentRepository.save(comment);
                result.setDeleted(true);
                deleted++;
            }
            results.add(result);
        }

        AiCommentModerationScanResponse response = new AiCommentModerationScanResponse();
        response.setScanned(results.size());
        response.setViolations(violations);
        response.setDeleted(deleted);
        response.setModel(safeModel);
        response.setItems(results);
        return response;
    }

    private AiCommentModerationResult moderate(Comment comment, String model) {
        AiCommentModerationResult result = new AiCommentModerationResult();
        result.setCommentId(comment.getId());
        result.setContent(comment.getContent());

        String prompt = """
                Bạn là bộ lọc kiểm duyệt bình luận cho ứng dụng nghe nhạc.
                Hãy xác định bình luận có vi phạm tiêu chuẩn cộng đồng hay không.
                Vi phạm gồm: spam/quảng cáo rác, xúc phạm hoặc quấy rối, thù ghét, đe dọa bạo lực, tình dục không phù hợp, nội dung tự hại, lộ thông tin cá nhân, nội dung bất hợp pháp.
                Chỉ trả về JSON hợp lệ theo mẫu:
                {"violates":true,"reason":"short reason"}
                hoặc
                {"violates":false,"reason":"ok"}

                Bình luận: "%s"
                """.formatted(comment.getContent().replace("\"", "\\\""));

        try {
            Map<String, Object> request = new HashMap<>();
            request.put("model", model);
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
            JsonNode json = objectMapper.readTree(extractJson(aiText));

            result.setViolates(json.path("violates").asBoolean(false));
            result.setReason(json.path("reason").asText("ok"));
            return result;
        } catch (Exception e) {
            result.setViolates(false);
            result.setReason("Ollama error");
            return result;
        }
    }

    private String extractJson(String text) {
        int start = text.indexOf('{');
        int end = text.lastIndexOf('}');
        if (start >= 0 && end > start) {
            return text.substring(start, end + 1);
        }
        return "{\"violates\":false,\"reason\":\"invalid ai response\"}";
    }
}
