package cl.megatech.catalogo.kafka.controller;

import cl.megatech.catalogo.kafka.service.PopularidadService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/catalogo/populares")
public class PopularidadController {

    private final PopularidadService popularidadService;

    public PopularidadController(
            PopularidadService popularidadService) {

        this.popularidadService = popularidadService;
    }

    @GetMapping
    public List<ProductoPopular> obtenerProductosPopulares() {

        return popularidadService.obtenerProductosPopulares()
                .entrySet()
                .stream()
                .sorted((a, b) ->
                        Integer.compare(b.getValue(), a.getValue()))
                .map(entry -> new ProductoPopular(
                        entry.getKey(),
                        entry.getValue()
                ))
                .toList();
    }

    public record ProductoPopular(
            Long productoId,
            Integer unidadesVendidas
    ) {
    }
}
