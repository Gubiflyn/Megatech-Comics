package cl.megatech.inventario.controller;

import cl.megatech.inventario.dto.InventarioRequest;
import cl.megatech.inventario.dto.StockRequest;
import cl.megatech.inventario.model.Inventario;
import cl.megatech.inventario.service.InventarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventario")
public class InventarioController {

    private final InventarioService inventarioService;

    public InventarioController(
            InventarioService inventarioService) {
        this.inventarioService = inventarioService;
    }

    @GetMapping
    public ResponseEntity<List<Inventario>> listarTodos() {
        return ResponseEntity.ok(
                inventarioService.listarTodos()
        );
    }

    @GetMapping("/producto/{productoId}")
    public ResponseEntity<Inventario> obtenerPorProducto(
            @PathVariable Long productoId) {

        return ResponseEntity.ok(
                inventarioService.obtenerPorProductoId(productoId)
        );
    }

    @PostMapping
    public ResponseEntity<Inventario> crear(
            @Valid @RequestBody InventarioRequest request) {

        Inventario inventario =
                inventarioService.crear(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(inventario);
    }

    @PutMapping("/producto/{productoId}")
    public ResponseEntity<Inventario> actualizar(
            @PathVariable Long productoId,
            @Valid @RequestBody InventarioRequest request) {

        return ResponseEntity.ok(
                inventarioService.actualizar(
                        productoId,
                        request
                )
        );
    }

    @PostMapping("/producto/{productoId}/descontar")
    public ResponseEntity<Inventario> descontar(
            @PathVariable Long productoId,
            @Valid @RequestBody StockRequest request) {

        return ResponseEntity.ok(
                inventarioService.descontarStock(
                        productoId,
                        request.getCantidad()
                )
        );
    }

    @PostMapping("/producto/{productoId}/reponer")
    public ResponseEntity<Inventario> reponer(
            @PathVariable Long productoId,
            @Valid @RequestBody StockRequest request) {

        return ResponseEntity.ok(
                inventarioService.reponerStock(
                        productoId,
                        request.getCantidad()
                )
        );
    }
}