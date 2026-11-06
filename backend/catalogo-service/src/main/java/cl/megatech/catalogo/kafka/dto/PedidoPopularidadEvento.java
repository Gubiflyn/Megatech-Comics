package cl.megatech.catalogo.kafka.dto;

import java.util.List;

public record PedidoPopularidadEvento(
        String eventoId,
        String tipo,
        Long pedidoId,
        String usuarioId,
        List<ItemPedido> items,
        String fecha
) {
    public record ItemPedido(
            Long productoId,
            Integer cantidad
    ) {
    }
}
