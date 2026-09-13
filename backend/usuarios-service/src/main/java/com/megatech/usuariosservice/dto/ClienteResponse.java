package com.megatech.usuariosservice.dto;

import com.megatech.usuariosservice.model.Cliente;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClienteResponse {

    private Long id;
    private String clienteUuid;
    private String email;
    private String nombreCompleto;
    private LocalDateTime fechaCreacion;

    public static ClienteResponse fromEntity(Cliente cliente) {
        return ClienteResponse.builder()
                .id(cliente.getId())
                .clienteUuid(cliente.getClienteUuid())
                .email(cliente.getEmail())
                .nombreCompleto(cliente.getNombreCompleto())
                .fechaCreacion(cliente.getFechaCreacion())
                .build();
    }
}
