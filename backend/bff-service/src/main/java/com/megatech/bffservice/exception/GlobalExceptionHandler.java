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

@RestControllerAdvice
public class GlobalExceptionHandler
        implements AuthenticationEntryPoint, AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    public GlobalExceptionHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException) throws IOException {

        writeError(
                response,
                request,
                HttpStatus.UNAUTHORIZED,
                resolveMessage(authException)
        );
    }

    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException accessDeniedException) throws IOException {

        writeError(
                response,
                request,
                HttpStatus.FORBIDDEN,
                "Access is denied for the current token"
        );
    }

    @ExceptionHandler(DownstreamServiceException.class)
    public ResponseEntity<?> handleDownstream(
            DownstreamServiceException ex,
            HttpServletRequest request) {

        /*
         * Si el microservicio respondió con un error de negocio
         * (404, 400, 409, etc.), mantenemos ese mismo status y body.
         */
        if (ex.isPassthrough() && ex.getStatus() != null) {

            String body = ex.getBody();

            if (body == null || body.isBlank()) {
                Map<String, Object> respuesta = new LinkedHashMap<>();
                respuesta.put("timestamp", Instant.now().toString());
                respuesta.put("status", ex.getStatus().value());
                respuesta.put("message", "Downstream service returned an error");
                respuesta.put("path", request.getRequestURI());

                return ResponseEntity
                        .status(ex.getStatus())
                        .body(respuesta);
            }

            return ResponseEntity
                    .status(ex.getStatus())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(body);
        }

        /*
         * Si el microservicio ni siquiera pudo ser alcanzado
         * (timeout, conexión rechazada, etc.), devolvemos 502.
         */
        Map<String, Object> body = errorBody(
                HttpStatus.BAD_GATEWAY,
                request,
                "Downstream service is unavailable"
        );

        return ResponseEntity
                .status(HttpStatus.BAD_GATEWAY)
                .body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleUnexpected(
            Exception ex,
            HttpServletRequest request) {

        Map<String, Object> body = errorBody(
                HttpStatus.INTERNAL_SERVER_ERROR,
                request,
                "An unexpected error occurred"
        );

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(body);
    }

    private String resolveMessage(AuthenticationException authException) {

        if (authException instanceof OAuth2AuthenticationException oAuth2Exception
                && oAuth2Exception.getError() != null
                && oAuth2Exception.getError().getDescription() != null) {

            return oAuth2Exception.getError().getDescription();
        }

        return authException.getMessage() != null
                ? authException.getMessage()
                : "Invalid or missing bearer token";
    }

    private Map<String, Object> errorBody(
            HttpStatus status,
            HttpServletRequest request,
            String message) {

        Map<String, Object> body = new LinkedHashMap<>();

        body.put("timestamp", Instant.now().toString());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message);
        body.put("path", request.getRequestURI());

        return body;
    }

    private void writeError(
            HttpServletResponse response,
            HttpServletRequest request,
            HttpStatus status,
            String message) throws IOException {

        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        response.getWriter().write(
                objectMapper.writeValueAsString(
                        errorBody(status, request, message)
                )
        );
    }
}