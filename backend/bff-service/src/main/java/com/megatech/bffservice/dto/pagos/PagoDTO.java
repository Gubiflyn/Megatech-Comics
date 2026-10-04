package com.megatech.bffservice.dto.pagos;

import java.time.LocalDateTime;

public record PagoDTO(
        Long id,
        Long pedidoId,
        String usuarioId,
        String metodoPago,
        String estado,
        String codigoTransaccion,
        String mensaje,
        LocalDateTime fechaCreacion) {
}
