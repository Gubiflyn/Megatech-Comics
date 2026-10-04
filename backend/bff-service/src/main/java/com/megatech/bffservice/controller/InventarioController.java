package com.megatech.bffservice.controller;

import com.megatech.bffservice.dto.inventario.InventarioDTO;
import com.megatech.bffservice.dto.inventario.InventarioRequest;
import com.megatech.bffservice.dto.inventario.StockRequest;
import com.megatech.bffservice.service.InventarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventario")
public class InventarioController {

    private final InventarioService inventarioService;

    public InventarioController(InventarioService inventarioService) {
        this.inventarioService = inventarioService;
    }

    @GetMapping
    public ResponseEntity<List<InventarioDTO>> listarTodos() {
        return ResponseEntity.ok(inventarioService.listarTodos());
    }

    @GetMapping("/producto/{productoId}")
    public ResponseEntity<InventarioDTO> obtenerPorProducto(@PathVariable Long productoId) {
        return ResponseEntity.ok(inventarioService.obtenerPorProducto(productoId));
    }

    @PostMapping
    public ResponseEntity<InventarioDTO> crear(@RequestBody InventarioRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(inventarioService.crear(request));
    }

    @PutMapping("/producto/{productoId}")
    public ResponseEntity<InventarioDTO> actualizar(@PathVariable Long productoId, @RequestBody InventarioRequest request) {
        return ResponseEntity.ok(inventarioService.actualizar(productoId, request));
    }

    @PostMapping("/producto/{productoId}/descontar")
    public ResponseEntity<InventarioDTO> descontar(@PathVariable Long productoId, @RequestBody StockRequest request) {
        return ResponseEntity.ok(inventarioService.descontar(productoId, request));
    }

    @PostMapping("/producto/{productoId}/reponer")
    public ResponseEntity<InventarioDTO> reponer(@PathVariable Long productoId, @RequestBody StockRequest request) {
        return ResponseEntity.ok(inventarioService.reponer(productoId, request));
    }
}
