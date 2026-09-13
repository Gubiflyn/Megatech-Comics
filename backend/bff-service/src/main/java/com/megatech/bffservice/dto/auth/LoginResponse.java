package com.megatech.bffservice.dto.auth;

public record LoginResponse(String token, String clienteUuid, String email, String nombreCompleto) {
}
