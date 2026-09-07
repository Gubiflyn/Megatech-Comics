package cl.megatech.carrito.controller;

import cl.megatech.carrito.dto.ActualizarCantidadRequest;
import cl.megatech.carrito.dto.AgregarItemRequest;
import cl.megatech.carrito.model.Carrito;
import cl.megatech.carrito.service.CarritoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/carritos")
public class CarritoController {

    private final CarritoService carritoService;

    public CarritoController(CarritoService carritoService) {
        this.carritoService = carritoService;
    }

    @GetMapping("/{usuarioId}")
    public ResponseEntity<Carrito> obtenerCarrito(
            @PathVariable String usuarioId) {

        return ResponseEntity.ok(
                carritoService.obtenerPorUsuario(usuarioId)
        );
    }

    @PostMapping("/{usuarioId}/items")
    public ResponseEntity<Carrito> agregarItem(
            @PathVariable String usuarioId,
            @Valid @RequestBody AgregarItemRequest request,
            @RequestHeader(
                    value = HttpHeaders.AUTHORIZATION,
                    required = false
            ) String authorizationHeader) {

        return ResponseEntity.ok(
                carritoService.agregarItem(
                        usuarioId,
                        request,
                        authorizationHeader
                )
        );
    }

    @PutMapping("/{usuarioId}/items/{productoId}")
    public ResponseEntity<Carrito> actualizarCantidad(
            @PathVariable String usuarioId,
            @PathVariable Long productoId,
            @Valid @RequestBody
            ActualizarCantidadRequest request,
            @RequestHeader(
                    value = HttpHeaders.AUTHORIZATION,
                    required = false
            ) String authorizationHeader) {

        return ResponseEntity.ok(
                carritoService.actualizarCantidad(
                        usuarioId,
                        productoId,
                        request.getCantidad(),
                        authorizationHeader
                )
        );
    }

    @DeleteMapping("/{usuarioId}/items/{productoId}")
    public ResponseEntity<Carrito> eliminarItem(
            @PathVariable String usuarioId,
            @PathVariable Long productoId) {

        return ResponseEntity.ok(
                carritoService.eliminarItem(
                        usuarioId,
                        productoId
                )
        );
    }

    @DeleteMapping("/{usuarioId}")
    public ResponseEntity<Void> vaciarCarrito(
            @PathVariable String usuarioId) {

        carritoService.vaciarCarrito(usuarioId);

        return ResponseEntity.noContent().build();
    }
}