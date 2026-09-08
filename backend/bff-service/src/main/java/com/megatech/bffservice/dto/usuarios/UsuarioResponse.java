package com.megatech.bffservice.dto.usuarios;

import java.time.LocalDateTime;
import java.util.List;

public record UsuarioResponse(
        Long id,
        String azureOid,
        String email,
        String nombreCompleto,
        LocalDateTime fechaCreacion,
        List<DireccionResponse> direcciones) {
}
