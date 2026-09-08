package com.megatech.usuariosservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioUpdateRequest {

    @NotBlank(message = "email es obligatorio")
    @Email(message = "email debe tener un formato valido")
    private String email;

    @NotBlank(message = "nombreCompleto es obligatorio")
    private String nombreCompleto;
}
