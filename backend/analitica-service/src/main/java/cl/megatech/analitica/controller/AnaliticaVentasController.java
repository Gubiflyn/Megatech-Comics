package cl.megatech.analitica.controller;

import cl.megatech.analitica.service.AnaliticaVentasService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/analitica/ventas")
public class AnaliticaVentasController {

    private final AnaliticaVentasService analiticaVentasService;

    public AnaliticaVentasController(
            AnaliticaVentasService analiticaVentasService) {

        this.analiticaVentasService = analiticaVentasService;
    }

    @GetMapping
    public Map<String, Object> obtenerResumenVentas() {

        return Map.of(
                "pedidosCreados",
                analiticaVentasService.obtenerPedidosCreados(),

                "pedidosPagados",
                analiticaVentasService.obtenerPedidosPagados(),

                "productosVendidos",
                analiticaVentasService.obtenerProductosVendidos()
        );
    }

    @GetMapping("/productos")
    public Map<Long, Integer> obtenerProductosVendidos() {

        return analiticaVentasService.obtenerProductosVendidos();
    }
}
