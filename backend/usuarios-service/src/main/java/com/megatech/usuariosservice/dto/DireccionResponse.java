package com.megatech.usuariosservice.dto;

import com.megatech.usuariosservice.model.Direccion;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DireccionResponse {

    private Long id;
    private String calle;
    private String comuna;
    private String ciudad;
    private boolean esPrincipal;

    public static DireccionResponse fromEntity(Direccion direccion) {
        return DireccionResponse.builder()
                .id(direccion.getId())
                .calle(direccion.getCalle())
                .comuna(direccion.getComuna())
                .ciudad(direccion.getCiudad())
                .esPrincipal(direccion.isEsPrincipal())
                .build();
    }
}
