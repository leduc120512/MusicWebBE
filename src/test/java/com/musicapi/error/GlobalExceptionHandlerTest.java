package com.musicapi.error;

import com.musicapi.dto.ApiResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

/**
 * The error contract: right status, envelope body, and no internal text.
 */
class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    private static void assertResponse(ResponseEntity<ApiResponse<Void>> response, HttpStatus status, String message) {
        assertThat(response.getStatusCode()).isEqualTo(status);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().isSuccess()).isFalse();
        assertThat(response.getBody().getData()).isNull();
        assertThat(response.getBody().getMessage()).isEqualTo(message);
    }

    @Test
    @DisplayName("an unreadable body is 400 and never echoes the Jackson error")
    void unreadableBodyDoesNotLeakTheParserMessage() {
        HttpMessageNotReadableException ex = new HttpMessageNotReadableException(
                "JSON parse error: Unexpected character ('u' (code 117)): was expecting double-quote",
                mock(HttpInputMessage.class));

        ResponseEntity<ApiResponse<Void>> response = handler.handleUnreadableBody(ex);

        assertResponse(response, HttpStatus.BAD_REQUEST, "Request body is missing or not valid JSON");
        assertThat(response.getBody().getMessage()).doesNotContain("JSON parse error");
    }

    @Test
    @DisplayName("an unexpected exception is 500 with a generic message")
    void unexpectedExceptionIsGeneric() {
        ResponseEntity<ApiResponse<Void>> response =
                handler.handleUnexpected(new IllegalStateException("could not open JDBC connection to music_db"));

        assertResponse(response, HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error");
        assertThat(response.getBody().getMessage()).doesNotContain("music_db");
    }

    @Test
    @DisplayName("a missing Authorization header is 401, other headers are 400")
    void missingHeaderStatusDependsOnTheHeader() throws Exception {
        Method method = GlobalExceptionHandlerTest.class.getDeclaredMethod("missingHeaderStatusDependsOnTheHeader");
        org.springframework.core.MethodParameter parameter =
                new org.springframework.core.MethodParameter(method, -1);

        assertResponse(handler.handleMissingHeader(new MissingRequestHeaderException("Authorization", parameter)),
                HttpStatus.UNAUTHORIZED, "Missing required header: Authorization");
        assertResponse(handler.handleMissingHeader(new MissingRequestHeaderException("X-Trace-Id", parameter)),
                HttpStatus.BAD_REQUEST, "Missing required header: X-Trace-Id");
    }

    @Test
    @DisplayName("a missing query parameter is 400 and names the parameter")
    void missingParameterNamesTheParameter() {
        assertResponse(handler.handleMissingParameter(new MissingServletRequestParameterException("keyword", "String")),
                HttpStatus.BAD_REQUEST, "Missing required parameter: keyword");
    }

    @Test
    @DisplayName("an unsupported verb is 405")
    void unsupportedVerbIs405() {
        assertResponse(handler.handleMethodNotSupported(new HttpRequestMethodNotSupportedException("DELETE")),
                HttpStatus.METHOD_NOT_ALLOWED, "Method DELETE is not supported on this endpoint");
    }

    @Test
    @DisplayName("an unknown path is 404 in the envelope, not the whitelabel body")
    void unknownPathIs404() {
        assertResponse(handler.handleNotFound(new NoResourceFoundException(
                        org.springframework.http.HttpMethod.GET, "/api/nope")),
                HttpStatus.NOT_FOUND, "Endpoint not found");
    }

    @Test
    @DisplayName("access denied is 403")
    void accessDeniedIs403() {
        assertResponse(handler.handleAccessDenied(new AccessDeniedException("nope")),
                HttpStatus.FORBIDDEN, "Access denied");
    }

    @Test
    @DisplayName("a ResponseStatusException keeps the status and reason chosen by the service")
    void responseStatusExceptionIsPassedThrough() {
        assertResponse(handler.handleResponseStatus(
                        new ResponseStatusException(HttpStatus.CONFLICT, "Email is already in use")),
                HttpStatus.CONFLICT, "Email is already in use");
    }

    @Test
    @DisplayName("a ResponseStatusException without a reason still yields a message")
    void responseStatusExceptionWithoutReasonHasAFallback() {
        assertResponse(handler.handleResponseStatus(new ResponseStatusException(HttpStatus.BAD_GATEWAY)),
                HttpStatus.BAD_GATEWAY, "Request failed");
    }
}
