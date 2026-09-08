package com.megatech.bffservice.dto.pedidos;

import java.time.LocalDateTime;
import java.util.List;

public record PedidoDTO(
        Long id,
        String usuarioId,
        String estado,
        LocalDateTime fechaCreacion,
        LocalDateTime fechaActualizacion,
        List<ItemPedidoDTO> items) {
}
