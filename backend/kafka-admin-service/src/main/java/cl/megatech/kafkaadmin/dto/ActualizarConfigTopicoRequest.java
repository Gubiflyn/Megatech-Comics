package cl.megatech.kafkaadmin.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActualizarConfigTopicoRequest {

    @NotEmpty(message = "debe incluir al menos una configuración")
    private Map<String, String> configuraciones;

}
