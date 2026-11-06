package cl.megatech.analitica.service;

import cl.megatech.analitica.dto.InventarioMovimientoEvento;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class AnaliticaStockService {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(AnaliticaStockService.class);

    private final AtomicInteger movimientosProcesados =
            new AtomicInteger();

    private final Map<Long, InventarioMovimientoEvento> ultimoMovimiento =
            new ConcurrentHashMap<>();

    private final Set<String> eventosProcesados =
            ConcurrentHashMap.newKeySet();

    public synchronized void procesar(InventarioMovimientoEvento evento) {

        validar(evento);

        if (eventosProcesados.contains(evento.eventoId())) {
            LOGGER.info(
                    "[ANALITICA STOCK] Evento duplicado ignorado: {}",
                    evento.eventoId()
            );
            return;
        }

        // Registrar el ultimo movimiento del producto.
        ultimoMovimiento.put(evento.productoId(), evento);

        movimientosProcesados.incrementAndGet();
        eventosProcesados.add(evento.eventoId());

        LOGGER.info(
                "[ANALITICA STOCK] productoId={} | anterior={} | actual={} | minimo={} | bajoMinimo={}",
                evento.productoId(),
                evento.stockAnterior(),
                evento.stockActual(),
                evento.stockMinimo(),
                evento.bajoMinimo()
        );
    }

    private void validar(InventarioMovimientoEvento evento) {

        if (evento == null) {
            throw new IllegalArgumentException("Evento de inventario nulo");
        }

        if (evento.eventoId() == null ||
                evento.eventoId().isBlank()) {
            throw new IllegalArgumentException("eventoId obligatorio");
        }

        if (evento.productoId() == null ||
                evento.productoId() <= 0) {
            throw new IllegalArgumentException("productoId invalido");
        }

        if (evento.stockAnterior() == null ||
                evento.stockAnterior() < 0) {
            throw new IllegalArgumentException("stockAnterior invalido");
        }

        if (evento.stockActual() == null ||
                evento.stockActual() < 0) {
            throw new IllegalArgumentException("stockActual invalido");
        }

        if (evento.stockMinimo() == null ||
                evento.stockMinimo() < 0) {
            throw new IllegalArgumentException("stockMinimo invalido");
        }

        if (evento.bajoMinimo() == null) {
            throw new IllegalArgumentException("bajoMinimo obligatorio");
        }

        if (evento.motivo() == null ||
                evento.motivo().isBlank()) {
            throw new IllegalArgumentException("motivo obligatorio");
        }

        if (evento.fecha() == null ||
                evento.fecha().isBlank()) {
            throw new IllegalArgumentException("fecha obligatoria");
        }
    }

    public int obtenerMovimientosProcesados() {
        return movimientosProcesados.get();
    }

    public Map<Long, InventarioMovimientoEvento> obtenerUltimosMovimientos() {
        return Map.copyOf(ultimoMovimiento);
    }

    public long obtenerProductosBajoMinimo() {
        return ultimoMovimiento.values()
                .stream()
                .filter(evento -> Boolean.TRUE.equals(evento.bajoMinimo()))
                .count();
    }
}
