package com.megatech.usuariosservice.dto;

import com.megatech.usuariosservice.model.Usuario;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioResponse {

    private Long id;
    private String azureOid;
    private String email;
    private String nombreCompleto;
    private LocalDateTime fechaCreacion;
    private List<DireccionResponse> direcciones;

    public static UsuarioResponse fromEntity(Usuario usuario) {
        return UsuarioResponse.builder()
                .id(usuario.getId())
                .azureOid(usuario.getAzureOid())
                .email(usuario.getEmail())
                .nombreCompleto(usuario.getNombreCompleto())
                .fechaCreacion(usuario.getFechaCreacion())
                .direcciones(usuario.getDirecciones() == null ? List.of() :
                        usuario.getDirecciones().stream()
                                .map(DireccionResponse::fromEntity)
                                .collect(Collectors.toList()))
                .build();
    }
}
