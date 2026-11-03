package cl.megatech.analitica.controller;

import cl.megatech.analitica.dto.InventarioMovimientoEvento;
import cl.megatech.analitica.service.AnaliticaStockService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/analitica/stock")
public class AnaliticaStockController {

    private final AnaliticaStockService analiticaStockService;

    public AnaliticaStockController(
            AnaliticaStockService analiticaStockService) {

        this.analiticaStockService = analiticaStockService;
    }

    @GetMapping
    public Map<String, Object> obtenerResumenStock() {

        return Map.of(
                "movimientosProcesados",
                analiticaStockService.obtenerMovimientosProcesados(),

                "productosBajoMinimo",
                analiticaStockService.obtenerProductosBajoMinimo(),

                "ultimosMovimientos",
                analiticaStockService.obtenerUltimosMovimientos()
        );
    }

    @GetMapping("/productos")
    public Map<Long, InventarioMovimientoEvento>
            obtenerUltimosMovimientos() {

        return analiticaStockService.obtenerUltimosMovimientos();
    }
}
