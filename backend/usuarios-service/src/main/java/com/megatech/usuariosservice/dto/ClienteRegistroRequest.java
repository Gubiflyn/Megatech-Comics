package com.megatech.usuariosservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClienteRegistroRequest {

    @NotBlank(message = "email es obligatorio")
    @Email(message = "email debe tener un formato valido")
    private String email;

    @NotBlank(message = "password es obligatorio")
    @Size(min = 8, message = "password debe tener al menos 8 caracteres")
    private String password;

    @NotBlank(message = "nombreCompleto es obligatorio")
    private String nombreCompleto;
}
