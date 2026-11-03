package cl.megatech.analitica.dto;

import java.util.List;

public record PedidoEvento(
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
