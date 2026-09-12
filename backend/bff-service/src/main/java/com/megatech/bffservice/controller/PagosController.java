package com.megatech.bffservice.controller;

import com.megatech.bffservice.dto.pagos.PagoDTO;
import com.megatech.bffservice.dto.pagos.ProcesarPagoRequest;
import com.megatech.bffservice.service.PagosService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pagos")
public class PagosController {

    private final PagosService pagosService;

    public PagosController(PagosService pagosService) {
        this.pagosService = pagosService;
    }

    @PostMapping("/pedido/{pedidoId}")
    public ResponseEntity<PagoDTO> procesarPago(
            @PathVariable Long pedidoId,
            @RequestBody ProcesarPagoRequest request,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        return ResponseEntity.status(HttpStatus.CREATED).body(pagosService.procesarPago(pedidoId, request, authorizationHeader));
    }

    @GetMapping("/{pagoId}")
    public ResponseEntity<PagoDTO> obtenerPago(
            @PathVariable Long pagoId,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        return ResponseEntity.ok(pagosService.obtenerPago(pagoId, authorizationHeader));
    }

    @GetMapping("/pedido/{pedidoId}")
    public ResponseEntity<List<PagoDTO>> obtenerPagosPedido(
            @PathVariable Long pedidoId,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        return ResponseEntity.ok(pagosService.obtenerPagosPedido(pedidoId, authorizationHeader));
    }
}
