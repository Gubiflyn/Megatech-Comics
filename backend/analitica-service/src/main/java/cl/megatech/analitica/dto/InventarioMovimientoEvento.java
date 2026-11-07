package cl.megatech.analitica.dto;

public record InventarioMovimientoEvento(
        String eventoId,
        String tipo,
        Long productoId,
        Integer stockAnterior,
        Integer stockActual,
        Integer stockMinimo,
        Boolean bajoMinimo,
        String motivo,
        String fecha
) {
}
