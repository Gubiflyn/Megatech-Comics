package cl.megatech.catalogo.kafka.service;

import cl.megatech.catalogo.kafka.dto.PedidoPopularidadEvento;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class PopularidadService {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(PopularidadService.class);

    private final Map<Long, AtomicInteger> unidadesVendidas =
            new ConcurrentHashMap<>();

    private final Set<String> eventosProcesados =
            ConcurrentHashMap.newKeySet();

    public synchronized void procesar(PedidoPopularidadEvento evento) {

        validar(evento);

        // Solo se contabilizan pedidos pagados.
        if ("PEDIDO_CREADO".equals(evento.tipo())) {
            LOGGER.info(
                    "[CATALOGO KAFKA] Pedido creado {}. Sin contabilizar venta.",
                    evento.pedidoId()
            );
            return;
        }

        if (eventosProcesados.contains(evento.eventoId())) {
            LOGGER.info(
                    "[CATALOGO KAFKA] Evento duplicado ignorado: {}",
                    evento.eventoId()
            );
            return;
        }

        for (PedidoPopularidadEvento.ItemPedido item : evento.items()) {
            unidadesVendidas
                    .computeIfAbsent(
                            item.productoId(),
                            id -> new AtomicInteger()
                    )
                    .addAndGet(item.cantidad());
        }

        eventosProcesados.add(evento.eventoId());

        LOGGER.info(
                "[CATALOGO KAFKA] Pedido pagado {} registrado. Popularidad actualizada.",
                evento.pedidoId()
        );
    }

    private void validar(PedidoPopularidadEvento evento) {

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

        for (PedidoPopularidadEvento.ItemPedido item : evento.items()) {
            if (item == null ||
                    item.productoId() == null ||
                    item.productoId() <= 0 ||
                    item.cantidad() == null ||
                    item.cantidad() <= 0) {
                throw new IllegalArgumentException(
                        "Item de pedido invalido"
                );
            }
        }
    }

    public Map<Long, Integer> obtenerProductosPopulares() {

        Map<Long, Integer> resultado = new HashMap<>();

        unidadesVendidas.forEach(
                (productoId, cantidad) ->
                        resultado.put(productoId, cantidad.get())
        );

        return resultado;
    }
}
