package com.megatech.bffservice.controller;

import com.megatech.bffservice.dto.pedidos.ActualizarEstadoRequest;
import com.megatech.bffservice.dto.pedidos.PedidoDTO;
import com.megatech.bffservice.service.PedidosService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pedidos")
public class PedidosController {

    private final PedidosService pedidosService;

    public PedidosController(PedidosService pedidosService) {
        this.pedidosService = pedidosService;
    }

    @PostMapping
    public ResponseEntity<PedidoDTO> crearPedido(
            @AuthenticationPrincipal Jwt jwt,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        return ResponseEntity.status(HttpStatus.CREATED).body(pedidosService.crearPedido(oidDe(jwt), authorizationHeader));
    }

    @GetMapping("/{pedidoId}")
    public ResponseEntity<PedidoDTO> obtenerPedido(
            @PathVariable Long pedidoId,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        return ResponseEntity.ok(pedidosService.obtenerPedido(pedidoId, authorizationHeader));
    }

    @GetMapping
    public ResponseEntity<List<PedidoDTO>> obtenerPedidosUsuario(
            @AuthenticationPrincipal Jwt jwt,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        return ResponseEntity.ok(pedidosService.obtenerPedidosUsuario(oidDe(jwt), authorizationHeader));
    }

    @PutMapping("/{pedidoId}/estado")
    public ResponseEntity<PedidoDTO> actualizarEstado(
            @PathVariable Long pedidoId,
            @RequestBody ActualizarEstadoRequest request,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        return ResponseEntity.ok(pedidosService.actualizarEstado(pedidoId, request, authorizationHeader));
    }

    private String oidDe(Jwt jwt) {
        return jwt.getClaimAsString("oid");
    }
}
