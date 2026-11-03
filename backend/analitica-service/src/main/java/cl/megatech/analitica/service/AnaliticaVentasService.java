package cl.megatech.analitica.service;

import cl.megatech.analitica.dto.PedidoEvento;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class AnaliticaVentasService {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(AnaliticaVentasService.class);

    private final AtomicInteger pedidosCreados = new AtomicInteger();
    private final AtomicInteger pedidosPagados = new AtomicInteger();

    private final Map<Long, AtomicInteger> productosVendidos =
            new ConcurrentHashMap<>();

    private final Set<String> eventosProcesados =
            ConcurrentHashMap.newKeySet();

    public void procesar(PedidoEvento evento) {

        validar(evento);

        // Evitar contar dos veces el mismo evento.
        if (!eventosProcesados.add(evento.eventoId())) {
            LOGGER.info(
                    "[ANALITICA] Evento duplicado ignorado: {}",
                    evento.eventoId()
            );
            return;
        }

        if ("PEDIDO_CREADO".equals(evento.tipo())) {

            pedidosCreados.incrementAndGet();

            for (PedidoEvento.ItemPedido item : evento.items()) {
                productosVendidos
                        .computeIfAbsent(
                                item.productoId(),
                                id -> new AtomicInteger()
                        )
                        .addAndGet(item.cantidad());
            }

        } else if ("PEDIDO_PAGADO".equals(evento.tipo())) {

            pedidosPagados.incrementAndGet();
        }

        LOGGER.info(
                "[ANALITICA VENTAS] pedidoId={} | tipo={} | creados={} | pagados={}",
                evento.pedidoId(),
                evento.tipo(),
                pedidosCreados.get(),
                pedidosPagados.get()
        );
    }

    private void validar(PedidoEvento evento) {

        if (evento == null) {
            throw new IllegalArgumentException("Evento nulo");
        }

        if (evento.eventoId() == null ||
                evento.eventoId().isBlank()) {
            throw new IllegalArgumentException("eventoId obligatorio");
        }

        if (evento.pedidoId() == null ||
                evento.pedidoId() <= 0) {
            throw new IllegalArgumentException("pedidoId invalido");
        }

        if (evento.usuarioId() == null ||
                evento.usuarioId().isBlank()) {
            throw new IllegalArgumentException("usuarioId obligatorio");
        }

        if (evento.fecha() == null ||
                evento.fecha().isBlank()) {
            throw new IllegalArgumentException("fecha obligatoria");
        }

        if (!"PEDIDO_CREADO".equals(evento.tipo()) &&
                !"PEDIDO_PAGADO".equals(evento.tipo())) {
            throw new IllegalArgumentException("Tipo de evento invalido");
        }

        if (evento.items() == null) {
            throw new IllegalArgumentException("items obligatorios");
        }

        for (PedidoEvento.ItemPedido item : evento.items()) {
            if (item == null ||
                    item.productoId() == null ||
                    item.productoId() <= 0 ||
                    item.cantidad() == null ||
                    item.cantidad() <= 0) {
                throw new IllegalArgumentException("Item de pedido invalido");
            }
        }
    }

    public int obtenerPedidosCreados() {
        return pedidosCreados.get();
    }

    public int obtenerPedidosPagados() {
        return pedidosPagados.get();
    }

    public Map<Long, Integer> obtenerProductosVendidos() {
        Map<Long, Integer> resultado = new java.util.HashMap<>();

        productosVendidos.forEach(
                (id, cantidad) -> resultado.put(id, cantidad.get())
        );

        return resultado;
    }
}
