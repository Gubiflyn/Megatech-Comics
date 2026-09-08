package cl.megatech.pedidos.controller;

import cl.megatech.pedidos.dto.ActualizarEstadoRequest;
import cl.megatech.pedidos.model.Pedido;
import cl.megatech.pedidos.service.PedidoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @PostMapping("/{usuarioId}")
    public ResponseEntity<Pedido> crearPedido(
            @PathVariable String usuarioId,
            @RequestHeader(
                    value = HttpHeaders.AUTHORIZATION,
                    required = false
            ) String authorizationHeader) {

        Pedido pedido = pedidoService.crearPedido(
                usuarioId,
                authorizationHeader
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(pedido);
    }

    @GetMapping("/{pedidoId}")
    public ResponseEntity<Pedido> obtenerPedido(
            @PathVariable Long pedidoId) {

        return ResponseEntity.ok(
                pedidoService.obtenerPorId(pedidoId)
        );
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<Pedido>> obtenerPedidosUsuario(
            @PathVariable String usuarioId) {

        return ResponseEntity.ok(
                pedidoService.obtenerPorUsuario(usuarioId)
        );
    }

    @PutMapping("/{pedidoId}/estado")
    public ResponseEntity<Pedido> actualizarEstado(
            @PathVariable Long pedidoId,
            @Valid @RequestBody
            ActualizarEstadoRequest request) {

        return ResponseEntity.ok(
                pedidoService.actualizarEstado(
                        pedidoId,
                        request.getEstado()
                )
        );
    }
}