package cl.megatech.kafkaadmin.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CrearTopicoRequest {

    @NotBlank(message = "no debe estar vacío")
    @Pattern(regexp = "^[a-zA-Z0-9._-]{1,249}$", message = "solo letras, números, '.', '_' y '-' (máx. 249 caracteres)")
    private String nombre;

    @Min(value = 1, message = "debe ser mayor o igual a 1")
    private int particiones = 1;

    @Min(value = 1, message = "debe ser mayor o igual a 1")
    private int factorReplicacion = 1;

    private Map<String, String> configuraciones;

}
