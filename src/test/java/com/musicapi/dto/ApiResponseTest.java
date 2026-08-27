package com.musicapi.dto;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Locks the response envelope every endpoint is expected to produce.
 *
 * The last test is the regression guard for the overload trap that shipped:
 * ApiResponse.success("User updated successfully") bound to success(T data),
 * so callers saw message "Success" with the real message stuffed into data.
 */
class ApiResponseTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    @DisplayName("serialises exactly success, message and data")
    void serialisesTheThreeEnvelopeFields() throws Exception {
        JsonNode json = mapper.readTree(mapper.writeValueAsString(ApiResponse.success("Loaded", 42)));

        assertThat(json.fieldNames()).toIterable().containsExactlyInAnyOrder("success", "message", "data");
        assertThat(json.get("success").asBoolean()).isTrue();
        assertThat(json.get("message").asText()).isEqualTo("Loaded");
        assertThat(json.get("data").asInt()).isEqualTo(42);
    }

    @Test
    @DisplayName("error() sets success=false and leaves data null")
    void errorLeavesDataNull() throws Exception {
        JsonNode json = mapper.readTree(mapper.writeValueAsString(ApiResponse.error("Nope")));

        assertThat(json.get("success").asBoolean()).isFalse();
        assertThat(json.get("message").asText()).isEqualTo("Nope");
        assertThat(json.get("data").isNull()).isTrue();
    }

    @Test
    @DisplayName("success(message, null) keeps the message in message, not in data")
    void messageOnlyResponseKeepsTheMessageWhereItBelongs() {
        ApiResponse<Object> response = ApiResponse.success("User deleted successfully", null);

        assertThat(response.getMessage()).isEqualTo("User deleted successfully");
        assertThat(response.getData()).isNull();
    }

    @Test
    @DisplayName("the single-argument overload is a data overload, never a message one")
    void singleArgumentOverloadCarriesData() {
        // Calling success("...") with one argument resolves to success(T data).
        // This test documents that, so nobody reintroduces the bug by assuming
        // the string becomes the message.
        ApiResponse<String> response = ApiResponse.success("some payload");

        assertThat(response.getData()).isEqualTo("some payload");
        assertThat(response.getMessage()).isEqualTo("Success");
    }
}
