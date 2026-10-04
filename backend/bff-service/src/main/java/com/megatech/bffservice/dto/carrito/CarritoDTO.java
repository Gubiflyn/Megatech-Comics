package com.megatech.bffservice.dto.carrito;

import java.time.LocalDateTime;
import java.util.List;

public record CarritoDTO(
        Long id,
        String usuarioId,
        String estado,
        LocalDateTime fechaCreacion,
        LocalDateTime fechaActualizacion,
        List<ItemCarritoDTO> items) {
}
