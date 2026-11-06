package cl.megatech.analitica.dto;

public record InventarioMovimientoEvento(
        String eventoId,
        Long productoId,
        Integer stockAnterior,
        Integer stockActual,
        Integer stockMinimo,
        Boolean bajoMinimo,
        String motivo,
        String fecha
) {
}
