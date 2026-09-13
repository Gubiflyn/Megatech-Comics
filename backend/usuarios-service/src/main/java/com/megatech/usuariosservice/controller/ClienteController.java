package com.megatech.usuariosservice.controller;

import com.megatech.usuariosservice.dto.ClienteLoginRequest;
import com.megatech.usuariosservice.dto.ClienteRegistroRequest;
import com.megatech.usuariosservice.dto.ClienteResponse;
import com.megatech.usuariosservice.service.ClienteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/clientes")
@RequiredArgsConstructor
public class ClienteController {

    private final ClienteService clienteService;

    @PostMapping("/registro")
    public ResponseEntity<ClienteResponse> registrarCliente(@Valid @RequestBody ClienteRegistroRequest request) {
        ClienteResponse response = clienteService.registrarCliente(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Uso exclusivo interno del bff-service; el frontend nunca llama este endpoint directamente.
     */
    @PostMapping("/validar-credenciales")
    public ResponseEntity<ClienteResponse> validarCredenciales(@Valid @RequestBody ClienteLoginRequest request) {
        return ResponseEntity.ok(clienteService.validarCredenciales(request));
    }
}
