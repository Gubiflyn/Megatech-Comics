package com.megatech.bffservice.exception;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Translates failures from calls to the domain microservices into clean
 * JSON responses instead of leaking raw stacktraces or reactive exceptions.
 * Kept separate from GlobalExceptionHandler (which owns auth failures and
 * the catch-all) since DownstreamServiceException is more specific and
 * Spring resolves @ExceptionHandler methods across all advice beans by
 * specificity, so this takes precedence for this exception type.
 */
@RestControllerAdvice
public class ProxyExceptionHandler {

    private static final int MAX_MESSAGE_LENGTH = 300;

    private final ObjectMapper objectMapper;

    public ProxyExceptionHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @ExceptionHandler(DownstreamServiceException.class)
    public ResponseEntity<Map<String, Object>> handleDownstreamServiceException(
            DownstreamServiceException ex, HttpServletRequest request) {

        HttpStatus status = ex.isPassthrough()
                ? HttpStatus.valueOf(ex.getStatus().value())
                : HttpStatus.BAD_GATEWAY;

        String message = ex.isPassthrough()
                ? extractMessage(ex.getBody())
                : "El servicio no respondió o no está disponible en este momento";

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", Instant.now().toString());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message);
        body.put("path", request.getRequestURI());
        return ResponseEntity.status(status).body(body);
    }

    private String extractMessage(String rawBody) {
        if (rawBody == null || rawBody.isBlank()) {
            return "El servicio respondió con un error";
        }
        try {
            JsonNode node = objectMapper.readTree(rawBody);
            if (node.hasNonNull("message")) {
                return node.get("message").asText();
            }
        } catch (Exception ignored) {
            // Body wasn't JSON (or didn't have a "message" field); fall back to raw text below.
        }
        return rawBody.length() > MAX_MESSAGE_LENGTH ? rawBody.substring(0, MAX_MESSAGE_LENGTH) : rawBody;
    }
}
