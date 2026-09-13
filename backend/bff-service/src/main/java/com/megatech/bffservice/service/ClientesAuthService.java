package com.megatech.bffservice.service;

import com.megatech.bffservice.client.DownstreamClient;
import com.megatech.bffservice.dto.auth.ClienteLoginRequest;
import com.megatech.bffservice.dto.auth.ClienteRegistroRequest;
import com.megatech.bffservice.dto.auth.ClienteResponse;
import com.megatech.bffservice.dto.auth.LoginResponse;
import com.megatech.bffservice.security.LocalJwtService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ClientesAuthService {

    private final DownstreamClient downstreamClient;
    private final LocalJwtService localJwtService;
    private final String baseUrl;

    public ClientesAuthService(DownstreamClient downstreamClient,
                                LocalJwtService localJwtService,
                                @Value("${app.services.usuarios-url}") String baseUrl) {
        this.downstreamClient = downstreamClient;
        this.localJwtService = localJwtService;
        this.baseUrl = baseUrl;
    }

    public ClienteResponse registrar(ClienteRegistroRequest request) {
        return downstreamClient.post(baseUrl + "/api/clientes/registro", request, null, ClienteResponse.class);
    }

    public LoginResponse login(ClienteLoginRequest request) {
        ClienteResponse cliente = downstreamClient.post(
                baseUrl + "/api/clientes/validar-credenciales", request, null, ClienteResponse.class);

        String token = localJwtService.generarToken(cliente.clienteUuid(), cliente.email());
        return new LoginResponse(token, cliente.clienteUuid(), cliente.email(), cliente.nombreCompleto());
    }
}
