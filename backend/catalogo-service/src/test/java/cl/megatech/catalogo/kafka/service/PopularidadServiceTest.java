
package cl.megatech.catalogo.kafka.service;

import cl.megatech.catalogo.kafka.dto.PedidoPopularidadEvento;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class PopularidadServiceTest {

    private PopularidadService service;

    @BeforeEach
    void preparar() {
        service = new PopularidadService();
    }

    private PedidoPopularidadEvento crearEvento(
            String eventoId,
            String tipo) {

        return new PedidoPopularidadEvento(
                eventoId,
                tipo,
                12L,
                "usuario-test",
                List.of(
                        new PedidoPopularidadEvento.ItemPedido(3L, 2),
                        new PedidoPopularidadEvento.ItemPedido(7L, 1)
                ),
                "2026-11-07T10:00:00Z"
        );
    }

    @Test
    void pedidoCreadoNoDebeContabilizarVentas() {

        service.procesar(
                crearEvento("evento-1", "PEDIDO_CREADO")
        );

        assertTrue(
                service.obtenerProductosPopulares().isEmpty()
        );
    }

    @Test
    void pedidoPagadoDebeContabilizarProductos() {

        service.procesar(
                crearEvento("evento-2", "PEDIDO_PAGADO")
        );

        assertEquals(
                Map.of(3L, 2, 7L, 1),
                service.obtenerProductosPopulares()
        );
    }

    @Test
    void debeAcumularVentasDeVariosPedidos() {

        service.procesar(
                crearEvento("evento-3", "PEDIDO_PAGADO")
        );

        service.procesar(
                crearEvento("evento-4", "PEDIDO_PAGADO")
        );

        assertEquals(
                Map.of(3L, 4, 7L, 2),
                service.obtenerProductosPopulares()
        );
    }

    @Test
    void noDebeContabilizarEventoDuplicado() {

        var evento = crearEvento(
                "evento-5", "PEDIDO_PAGADO"
        );

        service.procesar(evento);
        service.procesar(evento);

        assertEquals(
                Map.of(3L, 2, 7L, 1),
                service.obtenerProductosPopulares()
        );
    }

    @Test
    void debeRechazarTipoDeEventoInvalido() {

        var evento = crearEvento(
                "evento-6", "PEDIDO_CANCELADO"
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> service.procesar(evento)
        );

        assertTrue(
                service.obtenerProductosPopulares().isEmpty()
        );
    }

    @Test
    void debeRechazarCantidadInvalida() {

        var evento = new PedidoPopularidadEvento(
                "evento-7",
                "PEDIDO_PAGADO",
                12L,
                "usuario-test",
                List.of(
                        new PedidoPopularidadEvento.ItemPedido(3L, -2)
                ),
                "2026-11-07T10:00:00Z"
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> service.procesar(evento)
        );

        assertTrue(
                service.obtenerProductosPopulares().isEmpty()
        );
    }

    @Test
    void debeRechazarPedidoIdInvalido() {

        var evento = new PedidoPopularidadEvento(
                "evento-8",
                "PEDIDO_PAGADO",
                -1L,
                "usuario-test",
                List.of(
                        new PedidoPopularidadEvento.ItemPedido(3L, 2)
                ),
                "2026-11-07T10:00:00Z"
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> service.procesar(evento)
        );

        assertTrue(
                service.obtenerProductosPopulares().isEmpty()
        );
    }
}
