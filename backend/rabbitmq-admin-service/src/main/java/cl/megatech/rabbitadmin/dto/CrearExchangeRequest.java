package cl.megatech.rabbitadmin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CrearExchangeRequest {

    @NotBlank(message = "no debe estar vacío")
    private String nombre;

    @NotBlank(message = "no debe estar vacío")
    @Pattern(regexp = "direct|fanout|topic|headers", message = "debe ser uno de: direct, fanout, topic, headers")
    private String tipo;

    private boolean durable = true;

    private boolean autoDelete = false;

}
