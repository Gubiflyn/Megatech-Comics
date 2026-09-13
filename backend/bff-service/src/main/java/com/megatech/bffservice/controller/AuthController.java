package com.megatech.bffservice.controller;

import com.megatech.bffservice.dto.auth.ClienteLoginRequest;
import com.megatech.bffservice.dto.auth.ClienteRegistroRequest;
import com.megatech.bffservice.dto.auth.ClienteResponse;
import com.megatech.bffservice.dto.auth.LoginResponse;
import com.megatech.bffservice.service.ClientesAuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth/clientes")
public class AuthController {

    private final ClientesAuthService clientesAuthService;

    public AuthController(ClientesAuthService clientesAuthService) {
        this.clientesAuthService = clientesAuthService;
    }

    @PostMapping("/registro")
    public ResponseEntity<ClienteResponse> registro(@RequestBody ClienteRegistroRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(clientesAuthService.registrar(request));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody ClienteLoginRequest request) {
        return ResponseEntity.ok(clientesAuthService.login(request));
    }
}
