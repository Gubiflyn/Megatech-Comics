package cl.megatech.pedidos.messaging;

import java.util.List;

public record PedidoKafkaEvent(
        String eventoId, String tipo, Long pedidoId, String usuarioId,
        List<ItemPedido> items, String fecha
) {
    public record ItemPedido(Long productoId, Integer cantidad) {
    }
}
