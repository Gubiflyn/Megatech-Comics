package com.megatech.bffservice.dto.auth;

public record ClienteRegistroRequest(String email, String password, String nombreCompleto) {
}
