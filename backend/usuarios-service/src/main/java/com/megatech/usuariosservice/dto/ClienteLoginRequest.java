package com.megatech.usuariosservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClienteLoginRequest {

    @NotBlank(message = "email es obligatorio")
    private String email;

    @NotBlank(message = "password es obligatorio")
    private String password;
}
