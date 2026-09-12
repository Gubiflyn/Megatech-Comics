package com.megatech.usuariosservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DireccionRequest {

    @NotBlank(message = "calle es obligatoria")
    private String calle;

    @NotBlank(message = "comuna es obligatoria")
    private String comuna;

    @NotBlank(message = "ciudad es obligatoria")
    private String ciudad;

    private boolean esPrincipal;
}
