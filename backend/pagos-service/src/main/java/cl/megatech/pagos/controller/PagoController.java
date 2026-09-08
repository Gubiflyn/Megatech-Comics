package cl.megatech.pagos.controller;

import cl.megatech.pagos.dto.ProcesarPagoRequest;
import cl.megatech.pagos.model.Pago;
import cl.megatech.pagos.service.PagoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pagos")
public class PagoController {

    private final PagoService pagoService;

    public PagoController(PagoService pagoService) {
        this.pagoService = pagoService;
    }

    @PostMapping("/pedido/{pedidoId}")
    public ResponseEntity<Pago> procesarPago(
            @PathVariable Long pedidoId,
            @Valid @RequestBody
            ProcesarPagoRequest request,
            @RequestHeader(
                    value = HttpHeaders.AUTHORIZATION,
                    required = false
            ) String authorizationHeader) {

        Pago pago = pagoService.procesarPago(
                pedidoId,
                request,
                authorizationHeader
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(pago);
    }

    @GetMapping("/{pagoId}")
    public ResponseEntity<Pago> obtenerPago(
            @PathVariable Long pagoId) {

        return ResponseEntity.ok(
                pagoService.obtenerPorId(pagoId)
        );
    }

    @GetMapping("/pedido/{pedidoId}")
    public ResponseEntity<List<Pago>> obtenerPagosPedido(
            @PathVariable Long pedidoId) {

        return ResponseEntity.ok(
                pagoService.obtenerPorPedido(pedidoId)
        );
    }
}