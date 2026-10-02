package cl.megatech.rabbitadmin.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CrearColaRequest {

    @NotBlank(message = "no debe estar vacío")
    private String nombre;

    private boolean durable = true;

    private boolean exclusive = false;

    private boolean autoDelete = false;

}
