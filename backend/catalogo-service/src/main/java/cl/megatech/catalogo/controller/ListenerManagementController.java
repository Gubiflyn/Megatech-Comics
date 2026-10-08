package cl.megatech.catalogo.controller;

import cl.megatech.catalogo.service.ListenerManagementService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/internal/listeners")
public class ListenerManagementController {

    private final ListenerManagementService listenerService;

    public ListenerManagementController(
            ListenerManagementService listenerService) {

        this.listenerService = listenerService;
    }

    @GetMapping
    public ResponseEntity<?> listarListeners() {

        return ResponseEntity.ok(
                listenerService.listarListeners()
        );
    }

    @PostMapping("/{listenerId}/pause")
    public ResponseEntity<?> pausarListener(
            @PathVariable String listenerId) {

        boolean encontrado =
                listenerService.pausarListener(listenerId);

        if (!encontrado) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(
                Map.of(
                        "listenerId", listenerId,
                        "estado", "PAUSADO"
                )
        );
    }

    @PostMapping("/{listenerId}/resume")
    public ResponseEntity<?> reanudarListener(
            @PathVariable String listenerId) {

        boolean encontrado =
                listenerService.reanudarListener(listenerId);

        if (!encontrado) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(
                Map.of(
                        "listenerId", listenerId,
                        "estado", "ACTIVO"
                )
        );
    }
}