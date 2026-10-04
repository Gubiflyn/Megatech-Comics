package com.megatech.bffservice.dto.inventario;

import java.time.LocalDateTime;

public record InventarioDTO(Long id, Long productoId, Integer stock, Integer stockMinimo, LocalDateTime fechaActualizacion) {
}
