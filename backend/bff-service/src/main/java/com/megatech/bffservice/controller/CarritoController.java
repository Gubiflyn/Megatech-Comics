package com.megatech.bffservice.controller;

import com.megatech.bffservice.dto.carrito.ActualizarCantidadRequest;
import com.megatech.bffservice.dto.carrito.AgregarItemRequest;
import com.megatech.bffservice.dto.carrito.CarritoDTO;
import com.megatech.bffservice.service.CarritoService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/carrito")
public class CarritoController {

    private final CarritoService carritoService;

    public CarritoController(CarritoService carritoService) {
        this.carritoService = carritoService;
    }

    @GetMapping
    public ResponseEntity<CarritoDTO> obtenerCarrito(
            @AuthenticationPrincipal Jwt jwt,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        return ResponseEntity.ok(carritoService.obtenerCarrito(oidDe(jwt), authorizationHeader));
    }

    @PostMapping("/items")
    public ResponseEntity<CarritoDTO> agregarItem(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody AgregarItemRequest request,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        return ResponseEntity.ok(carritoService.agregarItem(oidDe(jwt), request, authorizationHeader));
    }

    @PutMapping("/items/{productoId}")
    public ResponseEntity<CarritoDTO> actualizarCantidad(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long productoId,
            @RequestBody ActualizarCantidadRequest request,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        return ResponseEntity.ok(carritoService.actualizarCantidad(oidDe(jwt), productoId, request, authorizationHeader));
    }

    @DeleteMapping("/items/{productoId}")
    public ResponseEntity<CarritoDTO> eliminarItem(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long productoId,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        return ResponseEntity.ok(carritoService.eliminarItem(oidDe(jwt), productoId, authorizationHeader));
    }

    @DeleteMapping
    public ResponseEntity<Void> vaciarCarrito(
            @AuthenticationPrincipal Jwt jwt,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        carritoService.vaciarCarrito(oidDe(jwt), authorizationHeader);
        return ResponseEntity.noContent().build();
    }

    private String oidDe(Jwt jwt) {
        return jwt.getClaimAsString("oid");
    }
}
