package cl.megatech.rabbitadmin.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CrearBindingRequest {

    @NotBlank(message = "no debe estar vacío")
    private String exchange;

    @NotBlank(message = "no debe estar vacío")
    private String cola;

    @NotBlank(message = "no debe estar vacío")
    private String routingKey;

}
