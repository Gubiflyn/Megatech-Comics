package com.megatech.bffservice.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.IOException;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Handles two distinct failure paths under one roof:
 *  - AuthenticationEntryPoint/AccessDeniedHandler: JWT validation failures
 *    (bad signature, expired token, issuer/audience mismatch) are thrown by
 *    Spring Security's filter chain *before* the request reaches a
 *    controller, so a plain @ExceptionHandler can never see them.
 *  - @RestControllerAdvice: anything else thrown from within a controller.
 */
@RestControllerAdvice
public class GlobalExceptionHandler implements AuthenticationEntryPoint, AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    public GlobalExceptionHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                          AuthenticationException authException) throws IOException {
        writeError(response, request, HttpStatus.UNAUTHORIZED, resolveMessage(authException));
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                        AccessDeniedException accessDeniedException) throws IOException {
        writeError(response, request, HttpStatus.FORBIDDEN, "Access is denied for the current token");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleUnexpected(Exception ex, HttpServletRequest request) {
        Map<String, Object> body = errorBody(HttpStatus.INTERNAL_SERVER_ERROR, request, "An unexpected error occurred");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }

    private String resolveMessage(AuthenticationException authException) {
        if (authException instanceof OAuth2AuthenticationException oAuth2Exception
                && oAuth2Exception.getError() != null
                && oAuth2Exception.getError().getDescription() != null) {
            return oAuth2Exception.getError().getDescription();
        }
        return authException.getMessage() != null ? authException.getMessage() : "Invalid or missing bearer token";
    }

    private Map<String, Object> errorBody(HttpStatus status, HttpServletRequest request, String message) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", Instant.now().toString());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message);
        body.put("path", request.getRequestURI());
        return body;
    }

    private void writeError(HttpServletResponse response, HttpServletRequest request,
                             HttpStatus status, String message) throws IOException {
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(objectMapper.writeValueAsString(errorBody(status, request, message)));
    }
}
