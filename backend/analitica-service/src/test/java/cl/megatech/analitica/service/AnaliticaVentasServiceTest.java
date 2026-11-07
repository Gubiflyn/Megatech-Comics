package cl.megatech.analitica.service;

import cl.megatech.analitica.dto.PedidoEvento;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class AnaliticaVentasServiceTest {

    private AnaliticaVentasService service;

    @BeforeEach
    void preparar() {
        service = new AnaliticaVentasService();
    }

    private PedidoEvento crearEvento(
            String eventoId,
            String tipo) {

        return new PedidoEvento(
                eventoId,
                tipo,
                12L,
                "usuario-test",
                List.of(
                        new PedidoEvento.ItemPedido(3L, 2),
                        new PedidoEvento.ItemPedido(7L, 1)
                ),
                "2026-11-07T10:00:00Z"
        );
    }

    @Test
    void debeRegistrarPedidoCreadoSinContarVentas() {

        service.procesar(
                crearEvento("evento-1", "PEDIDO_CREADO")
        );

        assertEquals(1, service.obtenerPedidosCreados());
        assertEquals(0, service.obtenerPedidosPagados());
        assertTrue(service.obtenerProductosVendidos().isEmpty());
    }

    @Test
    void debeRegistrarPedidoPagadoYProductosVendidos() {

        service.procesar(
                crearEvento("evento-2", "PEDIDO_PAGADO")
        );

        assertEquals(0, service.obtenerPedidosCreados());
        assertEquals(1, service.obtenerPedidosPagados());

        assertEquals(
                Map.of(3L, 2, 7L, 1),
                service.obtenerProductosVendidos()
        );
    }

    @Test
    void debeContabilizarCreadoYPagadoPorSeparado() {

        service.procesar(
                crearEvento("evento-3", "PEDIDO_CREADO")
        );

        service.procesar(
                crearEvento("evento-4", "PEDIDO_PAGADO")
        );

        assertEquals(1, service.obtenerPedidosCreados());
        assertEquals(1, service.obtenerPedidosPagados());
        assertEquals(2, service.obtenerProductosVendidos().get(3L));
    }

    @Test
    void noDebeContabilizarEventosDuplicados() {

        PedidoEvento evento =
                crearEvento("evento-5", "PEDIDO_PAGADO");

        service.procesar(evento);
        service.procesar(evento);

        assertEquals(1, service.obtenerPedidosPagados());
        assertEquals(2, service.obtenerProductosVendidos().get(3L));
    }

    @Test
    void debeRechazarTipoDeEventoInvalido() {

        PedidoEvento evento =
                crearEvento("evento-6", "PEDIDO_CANCELADO");

        assertThrows(
                IllegalArgumentException.class,
                () -> service.procesar(evento)
        );

        assertEquals(0, service.obtenerPedidosCreados());
        assertEquals(0, service.obtenerPedidosPagados());
    }

    @Test
    void debeRechazarItemsInvalidos() {

        PedidoEvento evento = new PedidoEvento(
                "evento-7",
                "PEDIDO_PAGADO",
                12L,
                "usuario-test",
                List.of(
                        new PedidoEvento.ItemPedido(3L, -2)
                ),
                "2026-11-07T10:00:00Z"
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> service.procesar(evento)
        );

        assertTrue(service.obtenerProductosVendidos().isEmpty());
    }
}
