package cl.megatech.inventario.messaging;

public record StockKafkaEvent(
        String eventoId, String tipo, Long productoId, Integer stockAnterior,
        Integer stockActual, Integer stockMinimo, boolean bajoMinimo, String motivo,
        String fecha
) {}
