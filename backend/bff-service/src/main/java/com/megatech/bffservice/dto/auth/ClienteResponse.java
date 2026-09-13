package com.megatech.bffservice.dto.auth;

import java.time.LocalDateTime;

public record ClienteResponse(
        Long id,
        String clienteUuid,
        String email,
        String nombreCompleto,
        LocalDateTime fechaCreacion) {
}
