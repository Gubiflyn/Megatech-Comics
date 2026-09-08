package com.megatech.bffservice.dto.usuarios;

public record UsuarioCreateRequest(String azureOid, String email, String nombreCompleto) {
}
