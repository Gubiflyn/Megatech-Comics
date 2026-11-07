
package cl.megatech.analitica.service;

import cl.megatech.analitica.dto.InventarioMovimientoEvento;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AnaliticaStockServiceTest {

    private AnaliticaStockService service;

    @BeforeEach
    void preparar() {
        service = new AnaliticaStockService();
    }

    private InventarioMovimientoEvento crearEvento(
            String eventoId,
            Long productoId,
            int stockAnterior,
            int stockActual,
            int stockMinimo,
            boolean bajoMinimo) {

        return new InventarioMovimientoEvento(
                eventoId,
                "STOCK_ACTUALIZADO",
                productoId,
                stockAnterior,
                stockActual,
                stockMinimo,
                bajoMinimo,
                "PEDIDO",
                "2026-11-07T10:00:00Z"
        );
    }

    @Test
    void debeRegistrarMovimientoDeStock() {

        var evento = crearEvento(
                "evento-1", 3L, 10, 9, 2, false
        );

        service.procesar(evento);

        assertEquals(1, service.obtenerMovimientosProcesados());
        assertEquals(0, service.obtenerProductosBajoMinimo());

        assertEquals(
                evento,
                service.obtenerUltimosMovimientos().get(3L)
        );
    }

    @Test
    void debeDetectarProductoBajoMinimo() {

        service.procesar(
                crearEvento("evento-2", 3L, 5, 1, 2, true)
        );

        assertEquals(1, service.obtenerProductosBajoMinimo());
    }

    @Test
    void debeActualizarUltimoMovimientoDelProducto() {

        service.procesar(
                crearEvento("evento-3", 3L, 10, 1, 2, true)
        );

        service.procesar(
                crearEvento("evento-4", 3L, 1, 8, 2, false)
        );

        assertEquals(2, service.obtenerMovimientosProcesados());
        assertEquals(0, service.obtenerProductosBajoMinimo());

        assertEquals(
                8,
                service.obtenerUltimosMovimientos()
                        .get(3L)
                        .stockActual()
        );
    }

    @Test
    void noDebeProcesarEventosDuplicados() {

        var evento = crearEvento(
                "evento-5", 3L, 10, 9, 2, false
        );

        service.procesar(evento);
        service.procesar(evento);

        assertEquals(1, service.obtenerMovimientosProcesados());
        assertEquals(1, service.obtenerUltimosMovimientos().size());
    }

    @Test
    void debeContabilizarVariosProductosBajoMinimo() {

        service.procesar(
                crearEvento("evento-6", 3L, 5, 1, 2, true)
        );

        service.procesar(
                crearEvento("evento-7", 7L, 4, 0, 2, true)
        );

        service.procesar(
                crearEvento("evento-8", 9L, 10, 8, 2, false)
        );

        assertEquals(3, service.obtenerMovimientosProcesados());
        assertEquals(2, service.obtenerProductosBajoMinimo());
    }

    @Test
    void debeRechazarTipoDeEventoInvalido() {

        var evento = new InventarioMovimientoEvento(
                "evento-9",
                "PEDIDO_CREADO",
                3L,
                10,
                9,
                2,
                false,
                "PEDIDO",
                "2026-11-07T10:00:00Z"
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> service.procesar(evento)
        );

        assertEquals(0, service.obtenerMovimientosProcesados());
    }

    @Test
    void debeRechazarStockNegativo() {

        var evento = crearEvento(
                "evento-10", 3L, 10, -1, 2, true
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> service.procesar(evento)
        );

        assertTrue(service.obtenerUltimosMovimientos().isEmpty());
        assertEquals(0, service.obtenerMovimientosProcesados());
    }
}
